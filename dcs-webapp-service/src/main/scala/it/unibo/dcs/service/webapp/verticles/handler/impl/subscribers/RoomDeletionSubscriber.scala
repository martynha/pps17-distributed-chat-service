package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.scala.core.Context
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.validation.ErrorTypes._
import it.unibo.dcs.exceptions._
import rx.lang.scala.Subscriber

import scala.language.postfixOps

final class RoomDeletionSubscriber(private[this] val routingContext: RoutingContext)
                                  (private[this] implicit val ctx: Context) extends Subscriber[Unit] {

  private implicit val context: RoutingContext = this.routingContext

  override def onCompleted(): Unit = routingContext response() end

  override def onError(error: Throwable): Unit = error match {

    case TokenCheckResponseException(message) =>
      endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
        missingResponseBody, message)

    case AuthServiceErrorException(errorJson) =>
      respond(HttpResponseStatus.BAD_REQUEST.code(), errorJson.encodePrettily())

    case RoomServiceErrorException(errorJson, _, _) =>
      respond(HttpResponseStatus.BAD_REQUEST.code(), errorJson.encodePrettily())

    case RoomDeletionResponseException(message) =>
      endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
        missingResponseBody, message)
  }

}

object RoomDeletionSubscriber {
  def apply(routingContext: RoutingContext)(implicit ctx: Context): RoomDeletionSubscriber =
    new RoomDeletionSubscriber(routingContext)
}

