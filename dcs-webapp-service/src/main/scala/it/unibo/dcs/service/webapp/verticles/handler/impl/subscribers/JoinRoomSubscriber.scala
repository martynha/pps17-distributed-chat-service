package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RoomJoinResult
import rx.lang.scala.Subscriber


final class JoinRoomSubscriber(protected override val response: HttpServerResponse)
  extends Subscriber[RoomJoinResult] with ErrorSubscriber {

  override def onNext(result: RoomJoinResult): Unit = response.end(result)
}

object JoinRoomSubscriber {
  def apply(response: HttpServerResponse): JoinRoomSubscriber = new JoinRoomSubscriber(response)
}


