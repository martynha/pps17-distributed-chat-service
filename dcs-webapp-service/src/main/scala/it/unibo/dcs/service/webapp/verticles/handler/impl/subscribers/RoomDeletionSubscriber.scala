package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.lang.scala.json.Json
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.exceptions._
import rx.lang.scala.Subscriber

import it.unibo.dcs.commons.VertxWebHelper.Implicits.jsonObjectToString

import scala.language.postfixOps

final class RoomDeletionSubscriber(protected override val response: HttpServerResponse) extends Subscriber[String]
  with ErrorSubscriber {

  override def onNext(name: String): Unit = response.end(Json.obj(("name", name)))

}

object RoomDeletionSubscriber {
  def apply(response: HttpServerResponse): RoomDeletionSubscriber =
    new RoomDeletionSubscriber(response)
}
