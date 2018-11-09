package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.GetUserResult
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import rx.lang.scala.Subscriber

final class GetUserSubscriber(protected override val response: HttpServerResponse)
  extends Subscriber[GetUserResult] with ErrorSubscriber with Logging {

  override def onNext(result: GetUserResult): Unit = {
    log.debug(s"Retrieving result: ${result.user}")
    response end result.encode()
  }

}

object GetUserSubscriber {
  def apply(response: HttpServerResponse): GetUserSubscriber = {
    new GetUserSubscriber(response)
  }
}
