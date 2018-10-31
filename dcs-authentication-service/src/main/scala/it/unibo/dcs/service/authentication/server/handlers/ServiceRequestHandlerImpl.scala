package it.unibo.dcs.service.authentication.server.handlers

import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.service.authentication.interactor.usecases._
import it.unibo.dcs.service.authentication.interactor.validations._
import it.unibo.dcs.service.authentication.request.Requests._
import it.unibo.dcs.service.authentication.server.containers.{UseCaseContainer, ValidationContainer}
import it.unibo.dcs.service.authentication.subscriber._

class ServiceRequestHandlerImpl(loginUserUseCase: LoginUserUseCase, logoutUserUseCase: LogoutUserUseCase,
                                registerUserUseCase: RegisterUserUseCase, checkTokenUseCase: CheckTokenUseCase,
                                deleteUserUseCase: DeleteUserUseCase,
                                logoutUserValidation: LogoutUserValidation,
                                registrationValidation: RegisterUserValidation,
                                loginValidation: LoginUserValidation,
                                deleteUserValidation: DeleteUserValidation) extends ServiceRequestHandler {

  override def handleRegistration(implicit context: RoutingContext): Unit = {
    val credentials: (Option[String], Option[String]) = getCredentials
    val request = RegisterUserRequest(credentials._1.get, credentials._2.get)
    registrationValidation(request)
      .subscribe(new RegistrationValiditySubscriber(context.response(), credentials, registerUserUseCase, request))
  }

  override def handleLogin(implicit context: RoutingContext): Unit = {
    val credentials = getCredentials
    val request = LoginUserRequest(credentials._1.get, credentials._2.get)
    loginValidation(request)
      .subscribe(new LoginValiditySubscriber(context.response(), credentials, loginUserUseCase, request))
  }

  override def handleLogout(implicit context: RoutingContext): Unit = {
    val request = LogoutUserRequest(getTokenFromHeader.get)
    /* Request check before executing logout*/
    logoutUserValidation(request)
      .subscribe(new LogoutValiditySubscriber(context.response(), logoutUserUseCase, request))
  }

  override def handleTokenCheck(implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    val username = context.request().getParam("username").head
    doIfDefined(token, checkTokenUseCase(CheckTokenRequest(token.get, username))
      .subscribe(new TokenCheckSubscriber(context.response())))
  }

  override def handleUserDeletion(implicit context: RoutingContext): Unit = {
    val request = DeleteUserRequest(getContextData("username").get, getTokenFromHeader.get)
    deleteUserValidation(request)
      .subscribe(new DeleteUserValiditySubscriber(context.response(), deleteUserUseCase, request))
  }

  private def getUsername(implicit context: RoutingContext): Option[String] =
    getJsonBodyData("username")

  private def getPassword(implicit context: RoutingContext): Option[String] =
    getJsonBodyData("password")

  private def getCredentials(implicit context: RoutingContext): (Option[String], Option[String]) =
    (getUsername, getPassword)

}

object ServiceRequestHandlerImpl {

  def apply(useCases: UseCaseContainer, validations: ValidationContainer): ServiceRequestHandlerImpl = {
    val loginUserUseCase = useCases.loginUserUseCase
    val logoutUserUseCase = useCases.logoutUserUseCase
    val registerUserUseCase = useCases.registerUserUseCase
    val checkTokenUseCase = useCases.checkTokenUseCase
    val deleteUserUseCase = useCases.deleteUserUseCase
    val logoutValidation = validations.logoutUserValidation
    val loginValidation = validations.loginUserValidation
    val registrationValidation = validations.registrationValidation
    val deleteUserValidation = validations.deleteUserValidation

    new ServiceRequestHandlerImpl(loginUserUseCase, logoutUserUseCase, registerUserUseCase, checkTokenUseCase,
      deleteUserUseCase, logoutValidation, registrationValidation, loginValidation, deleteUserValidation)
  }
}
