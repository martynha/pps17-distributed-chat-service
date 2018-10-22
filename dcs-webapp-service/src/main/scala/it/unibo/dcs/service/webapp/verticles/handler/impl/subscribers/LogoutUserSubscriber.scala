package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.scala.core.Context
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.validation.ErrorTypes.missingResponseBody
import it.unibo.dcs.exceptions.{AuthServiceErrorException, LogoutResponseException}
import rx.lang.scala.Subscriber

import scala.language.postfixOps

final class LogoutUserSubscriber(private[this] val context: RoutingContext)
                                (private[this] implicit val ctx: Context) extends Subscriber[Unit] {


  override def onCompleted(): Unit = context.response() end

  override def onError(error: Throwable): Unit = error match {
    case LogoutResponseException(message) =>
      endErrorResponse(context.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR, missingResponseBody, message)

    case AuthServiceErrorException(errorJson) =>
      implicit val context: RoutingContext = this.context
      respond(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), errorJson.encodePrettily())
  }
}

object LogoutUserSubscriber {

  def apply(context: RoutingContext)(implicit ctx: Context): LogoutUserSubscriber =
    new LogoutUserSubscriber(context)(ctx)
}