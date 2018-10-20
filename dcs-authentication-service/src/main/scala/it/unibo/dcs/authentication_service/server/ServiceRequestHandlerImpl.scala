package it.unibo.dcs.authentication_service.server

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.authentication_service.interactor._
import it.unibo.dcs.authentication_service.request.{CheckTokenRequest, LoginUserRequest, LogoutUserRequest, RegisterUserRequest}
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.validation.ErrorTypes.invalidToken
import rx.lang.scala.Subscriber

class ServiceRequestHandlerImpl(loginUserUseCase: LoginUserUseCase, logoutUserUseCase: LogoutUserUseCase,
                                registerUserUseCase: RegisterUserUseCase, checkTokenUseCase: CheckTokenUseCase,
                                logoutUserValidation: LogoutUserValidation)
  extends ServiceRequestHandler {

  override def handleRegistration(implicit context: RoutingContext): Unit = {
    val credentials = getCredentials
    doIfValidCredentials(credentials,
      registerUserUseCase(RegisterUserRequest(credentials._1.get, credentials._2.get))
        .subscribe(new TokenSubscriber("Username already taken", 409)))
  }

  override def handleLogin(implicit context: RoutingContext): Unit = {
    val credentials = getCredentials
    doIfValidCredentials(credentials,
      loginUserUseCase(LoginUserRequest(credentials._1.get, credentials._2.get))
        .subscribe(new TokenSubscriber("Wrong username or password", 401)))
  }

  override def handleLogout(implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    doIfDefined(token, logoutUserUseCase(LogoutUserRequest(token.get)).subscribe(new LogoutSubscriber))
  }

  override def handleTokenCheck(implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    doIfDefined(token, checkTokenUseCase(CheckTokenRequest(token.get))
      .subscribe(tokenIsValid => if (tokenIsValid) {
        respond(200, "Token is valid")
      } else {
        respond(401, "Token is invalid")
      }))
  }

  override def handleCheckLogout(implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    doIfDefined(token, logoutUserValidation(LogoutUserRequest(token.get)).subscribe(new LogoutValiditySubscriber))
  }

  private def getUsername(implicit context: RoutingContext): Option[String] = getJsonBodyData("username")

  private def getPassword(implicit context: RoutingContext): Option[String] = getJsonBodyData("password")

  private def getCredentials(implicit context: RoutingContext): (Option[String], Option[String]) =
    (getUsername, getPassword)

  private def doIfValidCredentials(credentials: (Option[String], Option[String]), action: => Unit)
                                  (implicit routingContext: RoutingContext): Unit = {
    if (credentials._1.isDefined && credentials._2.isDefined) {
      action
    } else respond(401, "Credentials not present")
  }

  private class TokenSubscriber(errorMessage: String, errorCode: Int)(implicit routingContext: RoutingContext)
    extends Subscriber[String] {
    override def onNext(token: String): Unit = respondWithToken(token, getUsername(routingContext).get)

    override def onError(error: Throwable): Unit = {
      error.printStackTrace()
      respond(errorCode, errorMessage)
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
      respond(400, "invalid token or user not logged in")
  }

  private class LogoutValiditySubscriber(implicit routingContext: RoutingContext) extends Subscriber[Unit] {

    override def onNext(unit: Unit): Unit = respondWithCode(200)

    override def onError(error: Throwable): Unit =
      endErrorResponse(routingContext.response(), HttpResponseStatus.BAD_REQUEST,
        errorType = invalidToken, description = "invalid token or user not logged in")
  }

}

object ServiceRequestHandlerImpl {
  def apply(loginUserUseCase: LoginUserUseCase, logoutUserUseCase: LogoutUserUseCase,
            registerUserUseCase: RegisterUserUseCase, checkTokenUseCase: CheckTokenUseCase,
            logoutUserValidation: LogoutUserValidation) =
    new ServiceRequestHandlerImpl(loginUserUseCase, logoutUserUseCase, registerUserUseCase, checkTokenUseCase,
      logoutUserValidation)
}
