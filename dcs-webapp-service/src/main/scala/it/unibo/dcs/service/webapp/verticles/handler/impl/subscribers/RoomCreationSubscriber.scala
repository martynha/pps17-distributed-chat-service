package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.JsonHelper.Implicits.jsonObjectToString
import it.unibo.dcs.commons.Logging
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.exceptions._
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RoomCreationResult
import rx.lang.scala.Subscriber

final class RoomCreationSubscriber(protected override val response: HttpServerResponse) extends Subscriber[RoomCreationResult]
  with ErrorSubscriber with Logging {

  override def onNext(result: RoomCreationResult): Unit = {
    val json: JsonObject = result
    response.setStatus(HttpResponseStatus.CREATED).end(json)
  }

}

object RoomCreationSubscriber {

  def apply(response: HttpServerResponse): RoomCreationSubscriber = new RoomCreationSubscriber(response)

}
