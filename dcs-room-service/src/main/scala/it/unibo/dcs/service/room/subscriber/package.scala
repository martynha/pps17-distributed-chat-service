package it.unibo.dcs.service.room

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.room.model.{Message, Participation, Room}
import it.unibo.dcs.service.room.subscriber.Implicits._
import rx.lang.scala.Subscriber

import scala.language.implicitConversions

package object subscriber {

  final class CreateRoomSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Room]
    with ErrorSubscriber with Logging {

    override def onNext(room: Room): Unit = {
      log.debug(s"Answering with room: $room")
      response setStatus HttpResponseStatus.CREATED endWith room
    }

  }

  final class JoinRoomSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Participation]
    with ErrorSubscriber with Logging {

    override def onNext(participation: Participation): Unit = {
      log.debug(s"Answering with participation: $participation")
      response setStatus HttpResponseStatus.CREATED endWith participation
    }
  }

  final class LeaveRoomSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Participation]
    with ErrorSubscriber with Logging {

    override def onNext(participation: Participation): Unit = {
      val json = Json.obj(("name", participation.room.name), ("username", participation.username))
      log.info(s"Answering with : $json")
      response.setStatus(HttpResponseStatus.OK).end(json.encode())
    }
  }

  final class CreateUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Unit]
    with ErrorSubscriber with Logging {

    override def onCompleted(): Unit = response setStatus HttpResponseStatus.CREATED end()

  }

  final class RoomParticipationsSubscriber(protected override val response: HttpServerResponse)
    extends Subscriber[Set[Participation]] with ErrorSubscriber with Logging {

    override def onNext(participations: Set[Participation]): Unit = {
      val results = Json.arr(participations.map(participationToJsonObject).toArray)
      response.end(results.encodePrettily())
    }
  }

  class DeleteRoomSubscriber(protected override val response: HttpServerResponse) extends Subscriber[String]
    with ErrorSubscriber {

    override def onNext(name: String): Unit =
      response endWith Json.obj(("name", name))

  }

  class GetRoomsSubscriber(protected override val response: HttpServerResponse) extends Subscriber[List[Room]]
    with ErrorSubscriber {

    override def onNext(rooms: List[Room]): Unit = response endWith rooms

  }

  class SendMessageSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Message]
    with ErrorSubscriber with Logging {

    override def onNext(message: Message): Unit = {
      val result: JsonObject = message
      log.info(s"Answering with room: $result")
      response.setStatus(HttpResponseStatus.CREATED).endWith(result)

    }

  }

  class GetUserParticipationsSubscriber(protected override val response: HttpServerResponse) extends Subscriber[List[Room]]
    with ErrorSubscriber with Logging {

    override def onNext(rooms: List[Room]): Unit = response endWith rooms

  }

  object Implicits {

    implicit def roomToJsonObject(room: Room): JsonObject = gson toJsonObject room

    implicit def roomsToJsonObject(rooms: Iterable[Room]): JsonArray = gson toJsonArray rooms

    implicit def participationToJsonObject(participation: Participation): JsonObject = gson toJsonObject participation

    implicit def messageToJsonObject(message: Message): JsonObject = gson toJsonObject message

  }

}