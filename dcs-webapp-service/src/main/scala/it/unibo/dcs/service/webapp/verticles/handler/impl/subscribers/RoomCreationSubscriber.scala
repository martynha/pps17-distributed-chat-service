package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.scala.core.Context
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.exceptions._
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RoomCreationResult
import rx.lang.scala.Subscriber


final class RoomCreationSubscriber(private[this] val routingContext: RoutingContext)
                                  (private[this] implicit val ctx: Context) extends Subscriber[RoomCreationResult] {


  override def onNext(result: RoomCreationResult): Unit = routingContext.response().end(result)

  override def onError(error: Throwable): Unit = error match {

    case TokenCheckResponseException(message) => ???

    case AuthServiceErrorException(errorJson) => ???

    case RoomServiceErrorException(errorJson) => ???

    case RoomCreationResponseException(message) => ???
  }

}

object RoomCreationSubscriber {
  def apply(routingContext: RoutingContext)(implicit ctx: Context): RoomCreationSubscriber =
    new RoomCreationSubscriber(routingContext)
}