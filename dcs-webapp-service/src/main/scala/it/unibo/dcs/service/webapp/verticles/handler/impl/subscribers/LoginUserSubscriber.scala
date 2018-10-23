package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.LoginResult
import rx.lang.scala.Subscriber

import scala.language.postfixOps

final class LoginUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[LoginResult]
  with ErrorSubscriber {

  override def onNext(value: LoginResult): Unit = response.end(value)

}

object LoginUserSubscriber {

  def apply(response: HttpServerResponse): LoginUserSubscriber = new LoginUserSubscriber(response)

}
