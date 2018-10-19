package it.unibo.dcs.service.webapp.verticles.handler.impl

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.Context
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.service.ErrorHandler
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository, UserRepository}
import it.unibo.dcs.service.webapp.usecases._
import it.unibo.dcs.service.webapp.verticles.handler.ServiceRequestHandler
import it.unibo.dcs.service.webapp.verticles.handler.impl.messages._

import scala.language.postfixOps

final class ServiceRequestHandlerImpl(private val userRepository: UserRepository,
                                      private val authRepository: AuthenticationRepository,
                                      private val roomRepository: RoomRepository)
  extends ServiceRequestHandler with ErrorHandler {

  private val logger = ScalaLogger.getLogger(getClass.getName)

  override def handleRegistration(context: RoutingContext)(implicit ctx: Context): Unit = {
    handle(context, registrationErrorMessage, request =>
      //TODO Error handling
      userRepository.checkUserRegistration(request).subscribe(_ => {
        val useCase = RegisterUserUseCase create(authRepository, userRepository, roomRepository)
        useCase(request) subscribe (result => context.response() end result)
      })
    )
  }


  override def handleLogout(context: RoutingContext)(implicit ctx: Context): Unit = {
    //TODO Request validity check
    handle(context, logoutErrorMessage, {
      val useCase = LogoutUserUseCase.create(authRepository)
      useCase(_) subscribe (_ => context response() end)
    })
  }


  override def handleLogin(context: RoutingContext)(implicit ctx: Context): Unit = {
    //TODO Request validity check
    handle(context, loginErrorMessage, {
      val useCase = LoginUserUseCase.create(authRepository, userRepository)
      useCase(_) subscribe (result => context response() end result)
    })
  }


  override def handleRoomCreation(context: RoutingContext)(implicit ctx: Context): Unit = {
    //TODO Request validity check
    handle(context, roomCreationErrorMessage, {
      val useCase = CreateRoomUseCase(authRepository, roomRepository)
      useCase(_) subscribe (result => context response() end result)
    })
  }

  override def handleRoomDeletion(context: RoutingContext)(implicit ctx: Context): Unit = {
    //TODO Request validity check
    handle(context, roomDeletionErrorMessage, {
      val useCase = DeleteRoomUseCase.create(authRepository, roomRepository)
      useCase(_) subscribe (_ => context response() end)
    })
  }

  private def handleRequestBody(context: RoutingContext, ifEmptyResponse: => Unit, handler: JsonObject => Unit): Unit = {
    context.getBodyAsJson().fold(ifEmptyResponse)(handler)
  }

  private def handle(context: RoutingContext, message: String, handler: JsonObject => Unit): Unit = {
    handleRequestBody(context, replyBadRequest(context, message), handler)
  }

  private def replyBadRequest(context: RoutingContext, response: String): Unit = {
    endErrorResponse(context.response(), HttpResponseStatus.BAD_REQUEST, "MISSING_REQUEST_BODY", response)
  }

}
