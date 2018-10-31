package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.Logging
import it.unibo.dcs.commons.VertxWebHelper.Implicits._
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RegisterResult
import rx.lang.scala.Subscriber

final class RegisterUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[RegisterResult]
  with ErrorSubscriber with Logging {

  override def onNext(result: RegisterResult): Unit = {
    val res: JsonObject = result
    log.info(s"Retrieving result: $res")
    response.setStatus(HttpResponseStatus.CREATED).end(res.encode())
  }

}

object RegisterUserSubscriber {
  def apply(response: HttpServerResponse): RegisterUserSubscriber =
    new RegisterUserSubscriber(response)
}
