package it.unibo.dcs.service.room

import com.google.gson.Gson
import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.JsonHelper
import it.unibo.dcs.commons.VertxWebHelper.Implicits.jsonObjectToString
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.room.interactor.usecases.{CreateRoomUseCase, CreateUserUseCase, DeleteRoomUseCase}
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest}
import it.unibo.dcs.service.room.subscriber.Implicits._
import rx.lang.scala.Subscriber

package object subscriber {

  final class CreateRoomSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Room]
    with ErrorSubscriber {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onNext(room: Room): Unit = {
      val json: JsonObject = room
      log.info(s"Answering with room: $json")
      response.end(json)
    }

  }

  final class CreateUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Unit]
    with ErrorSubscriber {

    override def onCompleted(): Unit = response.end()

  }

  final class CreateRoomValiditySubscriber(protected override val response: HttpServerResponse,
                                           request: CreateRoomRequest,
                                           createRoomUseCase: CreateRoomUseCase) extends Subscriber[Unit]
    with ErrorSubscriber {

    override def onCompleted(): Unit = createRoomUseCase(request, new CreateRoomSubscriber(response))

  }

  class DeleteRoomSubscriber(protected override val response: HttpServerResponse) extends Subscriber[String]
    with ErrorSubscriber {

    override def onNext(name: String): Unit = response.end(Json.obj(("name", name)))

  }

  final class CreateUserValiditySubscriber(protected override val response: HttpServerResponse,
                                           request: CreateUserRequest,
                                           createUserUseCase: CreateUserUseCase) extends Subscriber[Unit]
    with ErrorSubscriber {

    override def onCompleted(): Unit = createUserUseCase(request, new CreateUserSubscriber(response))

  }

  class DeleteRoomValiditySubscriber(protected override val response: HttpServerResponse,
                                     request: DeleteRoomRequest,
                                     deleteRoomUseCase: DeleteRoomUseCase) extends Subscriber[Unit]
    with ErrorSubscriber {

    override def onCompleted(): Unit = deleteRoomUseCase(request, new DeleteRoomSubscriber(response))

  }

  object Implicits {

    private val gson = new Gson

    implicit def roomToJsonObject(room: Room): JsonObject = JsonHelper.toJsonObject(gson, room)

  }

}
