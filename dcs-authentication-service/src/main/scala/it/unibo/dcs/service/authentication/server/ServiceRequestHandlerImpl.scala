package it.unibo.dcs.service.authentication.server

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxWebHelper.Implicits.{RichHttpServerResponse, jsonObjectToString}
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.exceptions.Implicits.throwableToJsonObject
import it.unibo.dcs.exceptions.{ErrorSubscriber, InvalidTokenException}
import it.unibo.dcs.service.authentication.interactor.usecases.{CheckTokenUseCase, LoginUserUseCase, LogoutUserUseCase, RegisterUserUseCase}
import it.unibo.dcs.service.authentication.interactor.validations.{LoginUserValidation, LogoutUserValidation, RegisterUserValidation}
import it.unibo.dcs.service.authentication.request.{CheckTokenRequest, LoginUserRequest, LogoutUserRequest, RegisterUserRequest}
import rx.lang.scala.Subscriber

class ServiceRequestHandlerImpl(loginUserUseCase: LoginUserUseCase, logoutUserUseCase: LogoutUserUseCase,
                                registerUserUseCase: RegisterUserUseCase, checkTokenUseCase: CheckTokenUseCase,
                                logoutUserValidation: LogoutUserValidation,
                                registrationValidation: RegisterUserValidation,
                                loginValidation: LoginUserValidation) extends ServiceRequestHandler {

  override def handleRegistration(implicit context: RoutingContext): Unit = {
    val credentials: (Option[String], Option[String]) = getCredentials
    registrationValidation(RegisterUserRequest(credentials._1.get, credentials._2.get), new RegistrationValiditySubscriber(context.response(), credentials))
  }

  override def handleLogin(implicit context: RoutingContext): Unit = {
    val credentials = getCredentials
    loginValidation(LoginUserRequest(credentials._1.get, credentials._2.get), new LoginValiditySubscriber(context.response(), credentials))
  }

  override def handleLogout(implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    /* Request check before execute logout*/
    logoutUserValidation(LogoutUserRequest(token.get), new LogoutValiditySubscriber(context.response()))
  }

  override def handleTokenCheck(implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    doIfDefined(token, checkTokenUseCase(CheckTokenRequest(token.get))
      .subscribe(tokenIsValid => if (tokenIsValid) {
        respond(HttpResponseStatus.OK)
      } else {
        val error: JsonObject = InvalidTokenException
        context.response().setStatus(HttpResponseStatus.UNAUTHORIZED).end(error)
      }))
  }

  private def getUsername(implicit context: RoutingContext): Option[String] = getJsonBodyData("username")

  private def getPassword(implicit context: RoutingContext): Option[String] = getJsonBodyData("password")

  private def getCredentials(implicit context: RoutingContext): (Option[String], Option[String]) =
    (getUsername, getPassword)

  private class TokenSubscriber(protected override val response: HttpServerResponse)
    extends Subscriber[String]
      with ErrorSubscriber {

    override def onNext(token: String): Unit = response.end(Json.obj(("token", token)))

  }

  private class LogoutSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Unit]
    with ErrorSubscriber {

    override def onCompleted(): Unit = response.setStatus(HttpResponseStatus.RESET_CONTENT).end()

  }

  private class LogoutValiditySubscriber(protected override val response: HttpServerResponse)(implicit context: RoutingContext) extends Subscriber[Unit]
    with ErrorSubscriber {

    override def onCompleted(): Unit =
      logoutUserUseCase(LogoutUserRequest(getTokenFromHeader.get)).subscribe(new LogoutSubscriber(response))

  }

  private class LoginValiditySubscriber(protected override val response: HttpServerResponse, credentials: (Option[String], Option[String]))
    extends Subscriber[Unit] with ErrorSubscriber {

    override def onCompleted(): Unit =
      loginUserUseCase(LoginUserRequest(credentials._1.get, credentials._2.get))
        .subscribe(new TokenSubscriber(response))

  }

  private class RegistrationValiditySubscriber(protected override val response: HttpServerResponse, credentials: (Option[String], Option[String]))
    extends Subscriber[Unit]  with ErrorSubscriber {

    override def onCompleted(): Unit =
      registerUserUseCase(RegisterUserRequest(credentials._1.get, credentials._2.get), new TokenSubscriber(response))
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
