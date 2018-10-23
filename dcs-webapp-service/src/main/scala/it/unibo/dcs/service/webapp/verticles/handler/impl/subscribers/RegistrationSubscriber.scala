package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.exceptions._
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RegisterResult
import rx.lang.scala.Subscriber

final class RegistrationSubscriber(protected override val response: HttpServerResponse) extends Subscriber[RegisterResult]
  with ErrorSubscriber {

  override def onNext(result: RegisterResult): Unit = response.end(result)

}

object RegistrationSubscriber {

  def apply(response: HttpServerResponse): RegistrationSubscriber =
    new RegistrationSubscriber(response)

}
