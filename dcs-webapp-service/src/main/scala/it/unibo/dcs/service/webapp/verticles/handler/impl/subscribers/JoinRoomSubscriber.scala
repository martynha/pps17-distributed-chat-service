package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.Publisher
import it.unibo.dcs.commons.VertxWebHelper.Implicits._
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RoomJoinResult
import rx.lang.scala.Subscriber


final class JoinRoomSubscriber(protected override val response: HttpServerResponse,
                               private[this] val publisher: Publisher)
  extends Subscriber[RoomJoinResult] with ErrorSubscriber {

  override def onNext(result: RoomJoinResult): Unit = {
    val res: JsonObject = result
    response.setStatus(HttpResponseStatus.CREATED).end(res.encode())
    publisher.publish(res)
  }
}

object JoinRoomSubscriber {
  def apply(response: HttpServerResponse,
            publisher: Publisher): JoinRoomSubscriber = new JoinRoomSubscriber(response, publisher)
}
