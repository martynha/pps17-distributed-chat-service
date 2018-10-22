package it.unibo.dcs.service.webapp.verticles.handler.impl

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.Context
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.validation.ErrorTypes._
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository, UserRepository}
import it.unibo.dcs.service.webapp.usecases._
import it.unibo.dcs.service.webapp.verticles.handler.ServiceRequestHandler
import it.unibo.dcs.service.webapp.verticles.handler.impl.message._
import it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers._

import scala.language.postfixOps

final class ServiceRequestHandlerImpl(private val userRepository: UserRepository,
                                      private val authRepository: AuthenticationRepository,
                                      private val roomRepository: RoomRepository) extends ServiceRequestHandler {


  override def handleRegistration(context: RoutingContext)(implicit ctx: Context): Unit =
    handle(context, missingRegistrationInfoMessage, {
      val useCase = RegisterUserUseCase.create(authRepository, userRepository, roomRepository)
      useCase(_) subscribe RegistrationSubscriber(context, userRepository, authRepository)
    })


  override def handleLogout(context: RoutingContext)(implicit ctx: Context): Unit =
    handle(context, missingLogoutInfoMessage, {
      val useCase = LogoutUserUseCase.create(authRepository)
      useCase(_) subscribe LogoutUserSubscriber(context)
    })


  override def handleLogin(context: RoutingContext)(implicit ctx: Context): Unit = {
    handle(context, missingLoginInfoMessage, {
      val useCase = LoginUserUseCase.create(authRepository, userRepository)
      useCase(_) subscribe LoginUserSubscriber(context)
    })
  }


  override def handleRoomCreation(context: RoutingContext)(implicit ctx: Context): Unit = {
    handle(context, missingRoomCreationInfoMessage, {
      val useCase = CreateRoomUseCase(authRepository, roomRepository)
      useCase(_) subscribe RoomCreationSubscriber(context)
    })
  }

  override def handleRoomDeletion(context: RoutingContext)(implicit ctx: Context): Unit = {
    handle(context, missingRoomDeletionInfoMessage, {
      val useCase = DeleteRoomUseCase.create(authRepository, roomRepository)
      useCase(_) subscribe RoomDeletionSubscriber(context)
    })
  }

  private def handle(context: RoutingContext, message: String, handler: JsonObject => Unit): Unit = {
    handleRequestBody(context, replyBadRequest(context, message), handler)
  }

  private def handleRequestBody(context: RoutingContext, ifEmptyResponse: => Unit, handler: JsonObject => Unit): Unit = {
    context.getBodyAsJson().fold(ifEmptyResponse)(handler)
  }

  private def replyBadRequest(context: RoutingContext, response: String): Unit = {
    endErrorResponse(context.response(), HttpResponseStatus.BAD_REQUEST, missingRequestBody, response)
  }

}
