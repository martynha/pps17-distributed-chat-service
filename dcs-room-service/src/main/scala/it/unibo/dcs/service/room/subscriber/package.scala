package it.unibo.dcs.service.room

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.service.room.interactor.usecases.{CreateRoomUseCase, CreateUserUseCase, DeleteRoomUseCase}
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest}
import rx.lang.scala.Subscriber
import it.unibo.dcs.service.room.subscriber.Implicits._
import it.unibo.dcs.commons.VertxWebHelper.endErrorResponse

import it.unibo.dcs.commons.validation.ErrorTypes._

package object subscriber {

  final class CreateRoomSubscriber (response: HttpServerResponse) extends Subscriber[Room] {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onNext(room: Room): Unit = {
      val json: JsonObject = room
      log.info(s"Answering with room: $json")
      response.end(json)
    }

  }

  final class CreateUserSubscriber(response: HttpServerResponse) extends Subscriber[Unit] {

    override def onCompleted(): Unit = response.end()

    override def onError(error: Throwable): Unit =
      endErrorResponse(response, HttpResponseStatus.INTERNAL_SERVER_ERROR, createUserError, error.getMessage)
  }

  class DeleteRoomSubscriber(response: HttpServerResponse) extends Subscriber[Unit] {

    override def onCompleted(): Unit = response.end()

    override def onError(error: Throwable): Unit =
      endErrorResponse(response, HttpResponseStatus.INTERNAL_SERVER_ERROR, deleteRoomError, error.getMessage)
  }

  final class CreateRoomValiditySubscriber(response: HttpServerResponse,
                                           request: CreateRoomRequest,
                                           createRoomUseCase: CreateRoomUseCase) extends Subscriber[Unit] {

    override def onCompleted(): Unit = createRoomUseCase(request, new CreateRoomSubscriber(response))

    override def onError(error: Throwable): Unit =
      endErrorResponse(response, HttpResponseStatus.BAD_REQUEST, createRoomError, error.getMessage)
  }

  final class CreateUserValiditySubscriber(response: HttpServerResponse,
                                           request: CreateUserRequest,
                                           createUserUseCase: CreateUserUseCase) extends Subscriber[Unit] {

    override def onCompleted(): Unit = createUserUseCase(request, new CreateUserSubscriber(response))

    override def onError(error: Throwable): Unit =
      endErrorResponse(response, HttpResponseStatus.BAD_REQUEST, createUserError, error.getMessage)
  }

  class DeleteRoomValiditySubscriber(response: HttpServerResponse,
                                     request: DeleteRoomRequest,
                                     deleteRoomUseCase: DeleteRoomUseCase) extends Subscriber[Unit] {

    override def onCompleted(): Unit = deleteRoomUseCase(request, new DeleteRoomSubscriber(response))

    override def onError(error: Throwable): Unit =
      endErrorResponse(response, HttpResponseStatus.BAD_REQUEST, deleteRoomError, error.getMessage)
  }

  object Implicits {

    implicit def roomToJsonObject(room: Room): JsonObject = {
      new JsonObject()
        .put("name", room.name)
        .put("owner_username", room.ownerUsername)
    }

    implicit def jsonObjectToString(json: JsonObject): String = json.encode()
  }
}
