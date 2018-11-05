package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.lang.scala.json.JsonArray
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.GetRoomParticipationsResult
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import rx.lang.scala.Subscriber

final class GetRoomParticipationsSubscriber(protected override val response: HttpServerResponse)
  extends Subscriber[GetRoomParticipationsResult] with ErrorSubscriber with Logging {

  override def onNext(result: GetRoomParticipationsResult): Unit = {
    val participationsArray: JsonArray = result
    log.debug(s"Retrieving result: $participationsArray")
    response end participationsArray.encodePrettily()
  }

}

object GetRoomParticipationsSubscriber {
  def apply(response: HttpServerResponse): GetRoomParticipationsSubscriber = {
    new GetRoomParticipationsSubscriber(response)
  }
}
