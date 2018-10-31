package it.unibo.dcs.service.room

import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.JsonHelper.Implicits.{RichGson, jsonObjectToString}
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.room.model.{Participation, Room}
import it.unibo.dcs.service.room.subscriber.Implicits._
import rx.lang.scala.Subscriber

import scala.language.implicitConversions

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

  final class JoinRoomSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Participation]
    with ErrorSubscriber {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onNext(participation: Participation): Unit = {
      val json: JsonObject = participation
      log.info(s"Answering with participation: $json")
      response.end(json)
    }
  }

  final class CreateUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Unit]
    with ErrorSubscriber {

    override def onCompleted(): Unit = response.end()

  }

  class DeleteRoomSubscriber(protected override val response: HttpServerResponse) extends Subscriber[String]
    with ErrorSubscriber {

    override def onNext(name: String): Unit = response.end(Json.obj(("name", name)))

  }

  class GetRoomsSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Set[Room]]
    with ErrorSubscriber {

    override def onNext(rooms: Set[Room]): Unit = {
      val results = new JsonArray()
      rooms.foreach(room => results.add(roomToJsonObject(room)))
      response.end(results.encodePrettily())
    }

  }

  class JoinRoomValiditySubscriber(protected override val response: HttpServerResponse,
                                   request: JoinRoomRequest,
                                   joinRoomUseCase: JoinRoomUseCase) extends Subscriber[Unit] with ErrorSubscriber {
    override def onCompleted(): Unit = joinRoomUseCase(request, new JoinRoomSubscriber(response))
  }

  object Implicits {

    implicit def roomToJsonObject(room: Room): JsonObject = gson toJsonObject room

    implicit def participationToJsonObject(participation: Participation): JsonObject = gson toJsonObject participation

    implicit def participationToJsonObject(participation: Participation): JsonObject = {
      new JsonObject()
        .put("username", participation.username)
        .put("name", participation.room.name)
        .put("join_date", participation.joinDate)
    }
  }

}
