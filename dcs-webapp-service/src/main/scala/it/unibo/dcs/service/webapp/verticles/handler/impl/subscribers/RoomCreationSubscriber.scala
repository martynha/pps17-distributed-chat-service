package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.scala.core.Context
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.validation.ErrorTypes.missingResponseBody
import it.unibo.dcs.exceptions._
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RoomCreationResult
import rx.lang.scala.Subscriber


final class RoomCreationSubscriber(private[this] val routingContext: RoutingContext)
                                  (private[this] implicit val ctx: Context) extends Subscriber[RoomCreationResult] {

  private implicit val context: RoutingContext = this.routingContext

  override def onNext(result: RoomCreationResult): Unit = routingContext.response().end(result)

  override def onError(error: Throwable): Unit = error match {

    case TokenCheckResponseException(message) =>
      endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
        missingResponseBody, message)

    case AuthServiceErrorException(errorJson) =>
      respond(HttpResponseStatus.BAD_REQUEST.code(), errorJson.encodePrettily())

    case RoomServiceErrorException(errorJson, _, _) =>
      respond(HttpResponseStatus.BAD_REQUEST.code(), errorJson.encodePrettily())

    case RoomCreationResponseException(message) =>
      endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
        missingResponseBody, message)
  }

}

object RoomCreationSubscriber {
  def apply(routingContext: RoutingContext)(implicit ctx: Context): RoomCreationSubscriber =
    new RoomCreationSubscriber(routingContext)
}