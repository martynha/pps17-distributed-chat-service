package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.Publisher
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions._
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RoomCreationResult
import rx.lang.scala.Subscriber

final class RoomCreationSubscriber(protected override val response: HttpServerResponse,
                                   private[this] val publisher: Publisher)
  extends Subscriber[RoomCreationResult] with ErrorSubscriber with Logging {

  override def onNext(result: RoomCreationResult): Unit = response setStatus HttpResponseStatus.CREATED endWith result

}

object RoomCreationSubscriber {

  def apply(response: HttpServerResponse, publisher: Publisher): RoomCreationSubscriber = new RoomCreationSubscriber(response, publisher)

}
