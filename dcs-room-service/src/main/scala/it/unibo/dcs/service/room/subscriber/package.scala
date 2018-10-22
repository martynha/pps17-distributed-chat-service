package it.unibo.dcs.service.room

import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.service.room.model.Room
import rx.lang.scala.Subscriber

import it.unibo.dcs.service.room.subscriber.Implicits._

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

    override def onNext(value: Unit): Unit = ()

    override def onCompleted(): Unit = response.end()

    override def onError(error: Throwable): Unit = ???

  }

  class DeleteRoomSubscriber (response: HttpServerResponse) extends Subscriber[Unit] {
    override def onNext(value: Unit): Unit = ()

    override def onCompleted(): Unit = response.end()

    override def onError(error: Throwable): Unit = ???
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
