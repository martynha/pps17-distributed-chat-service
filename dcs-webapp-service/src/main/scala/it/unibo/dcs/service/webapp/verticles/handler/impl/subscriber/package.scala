package it.unibo.dcs.service.webapp.verticles.handler.impl

import io.vertx.lang.scala.ScalaLogger
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.service.ErrorHandler
import rx.lang.scala.Subscriber

package object subscriber {

  final class ValidateRegistrationSubscriber(private[this] val response: HttpServerResponse)
    extends Subscriber[Unit] with ErrorHandler {

    private[this] val logger = ScalaLogger.getLogger(getClass.getName)

    override def onCompleted(): Unit = response.end()

    override def onError(error: Throwable): Unit = error match {
      case _ => ()
    }
  }

}
