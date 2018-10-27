package it.unibo.dcs.service.webapp.verticles.handler.impl

import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.Context
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxHelper.Implicits.RichEventBus
import it.unibo.dcs.exceptions.InternalException
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository, UserRepository}
import it.unibo.dcs.service.webapp.usecases._
import it.unibo.dcs.service.webapp.verticles.Addresses._
import it.unibo.dcs.service.webapp.verticles.handler.ServiceRequestHandler
import it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers._

import scala.language.postfixOps

final class ServiceRequestHandlerImpl(private[this] val eventBus: EventBus,
                                      private[this] val userRepository: UserRepository,
                                      private[this] val authRepository: AuthenticationRepository,
                                      private[this] val roomRepository: RoomRepository) extends ServiceRequestHandler {

  private[this] lazy val roomDeleted = eventBus.address(rooms.deleted)

  override def handleRegistration(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestBody(context) {
      val useCase = RegisterUserUseCase.create(authRepository, userRepository, roomRepository)
      useCase(_, RegisterUserSubscriber(context.response()))
    }

  override def handleLogout(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestBody(context) {
      val useCase = LogoutUserUseCase.create(authRepository)
      useCase(_, LogoutUserSubscriber(context.response()))
    }

  override def handleLogin(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestBody(context) {
      val useCase = LoginUserUseCase.create(authRepository, userRepository)
      useCase(_, LoginUserSubscriber(context.response))
    }

  override def handleRoomCreation(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestBody(context) {
      val useCase = CreateRoomUseCase(authRepository, roomRepository)
      useCase(_, RoomCreationSubscriber(context.response))
    }

  override def handleRoomDeletion(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestBody(context) {
      val useCase = DeleteRoomUseCase.create(authRepository, roomRepository)
      useCase(_, RoomDeletionSubscriber(context.response, roomDeleted))
    }

  override def handleJoinRoom(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestBody(context) {
      val useCase = JoinRoomUseCase(authRepository, roomRepository)
      useCase(_, JoinRoomSubscriber(context.response()))
    }

  override def handleGetRooms(context: RoutingContext)(implicit ctx: Context): Unit = {
    handleRequestParam(context, "user") {
      username => {
        handleRequestParam(context, "token") {
         token => {
           val useCase = GetRoomsUseCase(authRepository, roomRepository)
           useCase (new JsonObject().put("username", username).put("token", token), GetRoomsSubscriber(context.response))
         }
        }
      }
    }
  }

  private[this] def handleRequestBody(context: RoutingContext)(handler: JsonObject => Unit): Unit = 
    context.getBodyAsJson().fold(throw InternalException("Request body required"))(handler)

  private[this] def handleRequestParam(context: RoutingContext, param: String)(handler: String => Unit): Unit =
    context.request().getParam(param).fold(throw InternalException(s"Request param required: $param"))(handler)

}
