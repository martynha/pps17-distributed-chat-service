package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.scala.core.Context
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.validation.ErrorTypes
import it.unibo.dcs.commons.validation.ErrorTypes._
import it.unibo.dcs.exceptions.LogoutValidityResponseException
import it.unibo.dcs.service.webapp.interaction.Requests.LogoutUserRequest
import it.unibo.dcs.service.webapp.repositories.AuthenticationRepository
import it.unibo.dcs.service.webapp.usecases.LogoutUserUseCase
import rx.lang.scala.Subscriber

import scala.language.postfixOps

final class LogoutUserSubscriber(private[this] val response: HttpServerResponse,
                                 private[this] val request: LogoutUserRequest,
                                 private[this] val authRepository: AuthenticationRepository,
                                 private[this] implicit val ctx: Context) extends Subscriber[Unit] {


  override def onCompleted(): Unit = {
    val useCase = LogoutUserUseCase.create(authRepository)
    useCase(request) subscribe (_ => response end)
  }

  override def onError(error: Throwable): Unit = error match {
    case LogoutValidityResponseException(message) =>
      endErrorResponse(response, HttpResponseStatus.NO_CONTENT, errorType = missingResponseBody, message)
  }
}

object LogoutUserSubscriber {

  def apply(response: HttpServerResponse,
            request: LogoutUserRequest,
            authRepository: AuthenticationRepository)(implicit ctx: Context): LogoutUserSubscriber =
    new LogoutUserSubscriber(response, request, authRepository, ctx)
}