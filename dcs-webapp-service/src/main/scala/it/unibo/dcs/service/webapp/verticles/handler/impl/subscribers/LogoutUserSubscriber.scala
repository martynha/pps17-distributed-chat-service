package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.scala.core.Context
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.exceptions.ErrorSubscriber
import rx.lang.scala.Subscriber

import scala.language.postfixOps

final class LogoutUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[Unit]
                                with ErrorSubscriber {

  override def onCompleted(): Unit = response.end()

}

object LogoutUserSubscriber {

  def apply(response: HttpServerResponse): LogoutUserSubscriber = new LogoutUserSubscriber(response)

}