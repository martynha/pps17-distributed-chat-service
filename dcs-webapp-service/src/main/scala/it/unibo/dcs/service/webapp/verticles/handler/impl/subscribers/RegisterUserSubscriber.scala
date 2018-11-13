package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.Publisher
import it.unibo.dcs.commons.VertxWebHelper.Implicits._
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RegisterResult
import rx.lang.scala.Subscriber

final class RegisterUserSubscriber(protected override val response: HttpServerResponse,
                                   private[this] val publisher: Publisher) extends Subscriber[RegisterResult]
  with ErrorSubscriber with Logging {

  override def onNext(result: RegisterResult): Unit = {
    log.debug(s"Retrieving result: $result")
    response setStatus HttpResponseStatus.CREATED endWith result
    publisher publish[JsonObject] result
  }

}

object RegisterUserSubscriber {
  def apply(response: HttpServerResponse, publisher: Publisher): RegisterUserSubscriber =
    new RegisterUserSubscriber(response, publisher)
}
