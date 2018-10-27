package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.json.JsonArray
import it.unibo.dcs.commons.VertxWebHelper.Implicits.jsonArrayToString
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.GetRoomsResult
import rx.lang.scala.Subscriber
import it.unibo.dcs.service.webapp.interaction.Results.Implicits.getRoomsToJsonObject

final class GetRoomsSubscriber (protected override val response: HttpServerResponse)
  extends Subscriber[GetRoomsResult] with ErrorSubscriber {

  private val log = ScalaLogger.getLogger(getClass.getName)

  override def onNext(result: GetRoomsResult) : Unit = {
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