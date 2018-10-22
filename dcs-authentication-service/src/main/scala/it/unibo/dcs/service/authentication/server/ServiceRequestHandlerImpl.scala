package it.unibo.dcs.service.authentication.server

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.service.authentication.request
.{CheckTokenRequest, LoginUserRequest, LogoutUserRequest, RegisterUserRequest}
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.validation.ErrorTypes._
import it.unibo.dcs.service.authentication.interactor.usecases
  .{CheckTokenUseCase, LoginUserUseCase, LogoutUserUseCase, RegisterUserUseCase}
import it.unibo.dcs.service.authentication.interactor.validations
  .{LoginUserValidation, LogoutUserValidation, RegisterUserValidation}
import rx.lang.scala.Subscriber

class ServiceRequestHandlerImpl(loginUserUseCase: LoginUserUseCase, logoutUserUseCase: LogoutUserUseCase,
                                registerUserUseCase: RegisterUserUseCase, checkTokenUseCase: CheckTokenUseCase,
                                logoutUserValidation: LogoutUserValidation,
                                registrationValidation: RegisterUserValidation,
                                loginValidation: LoginUserValidation) extends ServiceRequestHandler {

  override def handleRegistration(implicit context: RoutingContext): Unit = {
    val credentials: (Option[String], Option[String]) = getCredentials
    registrationValidation(RegisterUserRequest(credentials._1.get, credentials._2.get))
      .subscribe(new RegistrationValiditySubscriber(credentials))
  }

  override def handleLogin(implicit context: RoutingContext): Unit = {
    val credentials = getCredentials
    loginValidation(LoginUserRequest(credentials._1.get, credentials._2.get))
      .subscribe(new LoginValiditySubscriber(credentials))
  }

  override def handleLogout(implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    /* Request check before execute logout*/
    logoutUserValidation(LogoutUserRequest(token.get)).subscribe(new LogoutValiditySubscriber)
  }

  override def handleTokenCheck(implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    doIfDefined(token, checkTokenUseCase(CheckTokenRequest(token.get))
      .subscribe(tokenIsValid => if (tokenIsValid) {
        respond(200, "Token is valid")
      } else {
        endErrorResponse(context.response(), HttpResponseStatus.UNAUTHORIZED,
          invalidToken, "Token is invalid")
      }))
  }

  private def getUsername(implicit context: RoutingContext): Option[String] = getJsonBodyData("username")

  private def getPassword(implicit context: RoutingContext): Option[String] = getJsonBodyData("password")

  private def getCredentials(implicit context: RoutingContext): (Option[String], Option[String]) =
    (getUsername, getPassword)

  private class TokenSubscriber(errorMessage: String, errorCode: Int)(implicit routingContext: RoutingContext)
    extends Subscriber[String] {
    override def onNext(token: String): Unit = respondWithToken(token, getUsername(routingContext).get)

    override def onError(error: Throwable): Unit = {
      error.printStackTrace()
      endErrorResponse(routingContext.response(), HttpResponseStatus.valueOf(errorCode),
        unexpectedTokenError, errorMessage)
    }

    private def respondWithToken(token: String, username: String)(implicit context: RoutingContext): Unit = {
      context.response
        .putHeader("content-type", "application/json")
        .setStatusCode(201)
        .end(Json.obj(("token", token)).encodePrettily())
    }
  }

  private class LogoutSubscriber(implicit routingContext: RoutingContext) extends Subscriber[Unit] {
    override def onNext(unit: Unit): Unit = respondWithCode(200)

    override def onError(error: Throwable): Unit =
      endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
        errorType = unexpectedLogoutError, description = error.getMessage)
  }

  private class LogoutValiditySubscriber(implicit routingContext: RoutingContext) extends Subscriber[Unit] {

    override def onCompleted(): Unit =
      logoutUserUseCase(LogoutUserRequest(getTokenFromHeader.get)).subscribe(new LogoutSubscriber)

    override def onError(error: Throwable): Unit =
      endErrorResponse(routingContext.response(), HttpResponseStatus.BAD_REQUEST,
        errorType = invalidToken, description = "Invalid token or user not logged in")
  }

  private class LoginValiditySubscriber(credentials: (Option[String], Option[String]))
                                       (implicit routingContext: RoutingContext) extends Subscriber[Unit] {

    override def onCompleted(): Unit =
      loginUserUseCase(LoginUserRequest(credentials._1.get, credentials._2.get))
        .subscribe(new TokenSubscriber("Wrong username or password", 401))

    override def onError(error: Throwable): Unit =
      endErrorResponse(routingContext.response(), HttpResponseStatus.UNAUTHORIZED,
        missingCredentials, "Login credentials not present")
  }

  private class RegistrationValiditySubscriber(credentials: (Option[String], Option[String]))
                                              (implicit routingContext: RoutingContext) extends Subscriber[Unit] {

    override def onCompleted(): Unit =
      registerUserUseCase(RegisterUserRequest(credentials._1.get, credentials._2.get))
        .subscribe(new TokenSubscriber("Username already taken", 409))

    override def onError(error: Throwable): Unit =
      endErrorResponse(routingContext.response(), HttpResponseStatus.UNAUTHORIZED,
        missingCredentials, "Registration credentials not present")
  }

}

object ServiceRequestHandlerImpl {

  def apply(loginUserUseCase: LoginUserUseCase, logoutUserUseCase: LogoutUserUseCase,
            registerUserUseCase: RegisterUserUseCase, checkTokenUseCase: CheckTokenUseCase,
            logoutUserValidation: LogoutUserValidation, registrationValidation: RegisterUserValidation,
            loginUserValidation: LoginUserValidation) =
    new ServiceRequestHandlerImpl(loginUserUseCase, logoutUserUseCase, registerUserUseCase, checkTokenUseCase,
      logoutUserValidation, registrationValidation, loginUserValidation)
}
