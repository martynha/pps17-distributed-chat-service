package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.lang.scala.json.Json
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.VertxWebHelper.Implicits.jsonObjectToString
import it.unibo.dcs.commons.Publisher
import it.unibo.dcs.exceptions._
import rx.lang.scala.Subscriber

import scala.language.postfixOps

final class RoomDeletionSubscriber(protected override val response: HttpServerResponse,
                                   private[this] val publisher: Publisher) extends Subscriber[String]
  with ErrorSubscriber {

  override def onNext(name: String): Unit = {
    response.end()
    publisher.publish(Json.obj(("name", name)))
  }

}

object RoomDeletionSubscriber {

  def apply(response: HttpServerResponse, publisher: Publisher): RoomDeletionSubscriber =
    new RoomDeletionSubscriber(response, publisher)

}
