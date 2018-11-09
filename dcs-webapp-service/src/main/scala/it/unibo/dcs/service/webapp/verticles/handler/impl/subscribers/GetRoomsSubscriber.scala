package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.GetRoomsResult
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import rx.lang.scala.Subscriber

final class GetRoomsSubscriber(protected override val response: HttpServerResponse)
  extends Subscriber[GetRoomsResult] with ErrorSubscriber with Logging {

  override def onNext(result: GetRoomsResult): Unit = {
    log.debug(s"Retrieving result: $result")
    response endWith result
  }

}

object GetRoomsSubscriber {
  def apply(response: HttpServerResponse): GetRoomsSubscriber = {
    new GetRoomsSubscriber(response)
  }
}