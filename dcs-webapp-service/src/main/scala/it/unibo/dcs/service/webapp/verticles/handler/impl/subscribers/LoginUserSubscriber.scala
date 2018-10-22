package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers


import io.netty.handler.codec.http.HttpResponseStatus._
import io.vertx.scala.core.Context
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxWebHelper.{endErrorResponse, respond}
import it.unibo.dcs.commons.validation.ErrorTypes.missingResponseBody
import it.unibo.dcs.exceptions.{AuthServiceErrorException, LoginResponseException}
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.LoginResult
import rx.lang.scala.Subscriber

import scala.language.postfixOps

final class LoginUserSubscriber(private[this] val context: RoutingContext)
                               (private[this] implicit val ctx: Context) extends Subscriber[LoginResult] {

  override def onNext(value: LoginResult): Unit = context.response().end(value)

  override def onError(error: Throwable): Unit = error match {
    case LoginResponseException(message) =>
      endErrorResponse(context.response(), httpResponseStatus = INTERNAL_SERVER_ERROR,
        errorType = missingResponseBody, description = message)

    case AuthServiceErrorException(errorJson) =>
      implicit val context: RoutingContext = this.context
      respond(INTERNAL_SERVER_ERROR.code(), errorJson.encodePrettily())
  }
}

object LoginUserSubscriber {

  def apply(routingContext: RoutingContext)(implicit ctx: Context): LoginUserSubscriber =
    new LoginUserSubscriber(routingContext)(ctx)
}
