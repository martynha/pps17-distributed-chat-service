package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.lang.scala.json.JsonArray
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.JsonHelper.Implicits.jsonArrayToString
import it.unibo.dcs.commons.Logging
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.GetRoomsResult
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import rx.lang.scala.Subscriber

final class GetRoomsSubscriber (protected override val response: HttpServerResponse)
  extends Subscriber[GetRoomsResult] with ErrorSubscriber with Logging {

  override def onNext(result: GetRoomsResult): Unit = {
    val json: JsonArray = result
    log.debug(s"Retrieving result: $json")
    response end json
  }

}

object GetRoomsSubscriber {
  def apply(response: HttpServerResponse): GetRoomsSubscriber = {
    new GetRoomsSubscriber(response)
  }
}