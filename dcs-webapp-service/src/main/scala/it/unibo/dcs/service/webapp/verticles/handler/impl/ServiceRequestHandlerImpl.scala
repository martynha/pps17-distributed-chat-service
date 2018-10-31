package it.unibo.dcs.service.webapp.verticles.handler.impl

import io.vertx.core.http.HttpHeaders
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.core.Context
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxHelper.Implicits.RichEventBus
import it.unibo.dcs.exceptions.InternalException
import it.unibo.dcs.service.webapp.interaction.Labels.JsonLabels._
import it.unibo.dcs.service.webapp.interaction.Labels.{JsonLabels, ParamLabels}
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

  private[this] lazy val roomDeleted = eventBus.address(Rooms.deleted)
  private[this] lazy val roomJoined = eventBus.address(Rooms.joined)

  override def handleRegistration(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestBody(context) {
      val useCase = RegisterUserUseCase.create(authRepository, userRepository, roomRepository)
      useCase(_, RegisterUserSubscriber(context.response()))
    }

  override def handleLogout(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestToken(context) {
      token =>
        handleRequestBody(context) {
          request =>
            val useCase = LogoutUserUseCase.create(authRepository)
            useCase(request.put(authenticationLabel, token), LogoutUserSubscriber(context.response()))
        }
    }

  override def handleLogin(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestBody(context) {
      val useCase = LoginUserUseCase.create(authRepository, userRepository)
      useCase(_, LoginUserSubscriber(context.response))
    }

  override def handleRoomCreation(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestToken(context) {
      token =>
        handleRequestBody(context) {
          request =>
            val useCase = CreateRoomUseCase(authRepository, roomRepository)
            useCase(request.put(authenticationLabel, token), RoomCreationSubscriber(context.response))
        }
    }

  override def handleRoomDeletion(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestToken(context) {
      token =>
        handleRequestParam(context, ParamLabels.roomNameLabel) {
          roomName =>
            handleRequestBody(context) {
              request =>
                val useCase = DeleteRoomUseCase.create(authRepository, roomRepository)
                useCase(request.put(JsonLabels.roomNameLabel, roomName).put(JsonLabels.authenticationLabel, token),
                  RoomDeletionSubscriber(context.response, roomDeleted))
            }
        }
    }

  override def handleJoinRoom(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestToken(context) {
      token =>
        handleRequestParam(context, ParamLabels.roomNameLabel) {
          roomName =>
            handleRequestBody(context) {
              request =>
                val useCase = JoinRoomUseCase(authRepository, roomRepository)
                useCase(request.put(JsonLabels.roomNameLabel, roomName).put(JsonLabels.authenticationLabel, token),
                  JoinRoomSubscriber(context.response(), roomJoined))
            }
        }
    }

  override def handleGetRooms(context: RoutingContext)(implicit ctx: Context): Unit =
    handleRequestParam(context, ParamLabels.userLabel) {
      username => {
        handleRequestParam(context, ParamLabels.tokenLabel) {
          token => {
            val useCase = GetRoomsUseCase(authRepository, roomRepository)
            useCase(Json.obj((usernameLabel, username), (tokenLabel, token)), GetRoomsSubscriber(context.response))
          }
        }
      }
    }

  private[this] def handleRequestBody(context: RoutingContext)(handler: JsonObject => Unit): Unit =
    context.getBodyAsJson().fold(throw InternalException("Request body required"))(handler)

  private[this] def handleRequestParam(context: RoutingContext, param: String)(handler: String => Unit): Unit =
    context.request().getParam(param).fold(throw InternalException(s"Request param required: $param"))(handler)

  private[this] def handleRequestHeader(context: RoutingContext, header: String)(handler: String => Unit): Unit =
    context.request().getHeader(header).fold(throw InternalException("Authorization token required"))(handler)

  private[this] def handleRequestToken(context: RoutingContext)(handler: String => Unit): Unit = {
    handleRequestHeader(context, HttpHeaders.AUTHORIZATION.toString)(handler)
  }
}
