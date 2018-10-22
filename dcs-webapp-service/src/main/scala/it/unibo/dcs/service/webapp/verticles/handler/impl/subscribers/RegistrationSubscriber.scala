package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.scala.core.Context
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.validation.ErrorTypes._
import it.unibo.dcs.exceptions._
import it.unibo.dcs.service.webapp.interaction.Requests.DeleteUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.RegisterResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import rx.lang.scala.Subscriber


final class RegistrationSubscriber(private[this] val routingContext: RoutingContext,
                                   private[this] val authRepository: AuthenticationRepository,
                                   private[this] val userRepository: UserRepository,
                                   private[this] implicit val ctx: Context) extends Subscriber[RegisterResult] {

  private implicit val routeContext: RoutingContext = this.routingContext

  override def onNext(result: RegisterResult): Unit = routingContext.response().end(result)

  override def onError(error: Throwable): Unit = error match {

    case AuthRegistrationResponseException(message) =>
      endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
        missingResponseBody, message)

    case AuthServiceErrorException(errorJson) =>
      respond(HttpResponseStatus.BAD_REQUEST.code(), errorJson.encodePrettily())

    case UserServiceErrorException(errorResponseJson, username, token) =>
      rollbackAuthRepository(username, token)(new RollbackOnServiceErrorSubscriber())

      class RollbackOnServiceErrorSubscriber() extends Subscriber[Unit] {
        override def onCompleted(): Unit =
          respond(HttpResponseStatus.BAD_REQUEST.code(), errorResponseJson.encodePrettily())

        /* We could retry to rollback... */
        override def onError(error: Throwable): Unit =
          endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
            userServiceWrongResponse, error.getMessage)
      }

    case UserCreationResponseException(message, username, token) =>
      rollbackAuthRepository(username, token)(new RollbackOnWrongResponseSubscriber())

      class RollbackOnWrongResponseSubscriber() extends Subscriber[Unit] {
        override def onCompleted(): Unit =
          endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
            userServiceWrongResponse, message)

        /* We could retry to rollback... */
        override def onError(error: Throwable): Unit =
          endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
            userServiceWrongResponse, error.getMessage)
      }

    case RoomServiceErrorException(errorJson, username, token) =>
      rollbackRepositories(username, token)
      respond(HttpResponseStatus.BAD_REQUEST.code(), errorJson.encodePrettily())

    case RegistrationResponseException(message, username, token) =>
      rollbackRepositories(username, token)
      endErrorResponse(routingContext.response(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
        missingResponseBody, message)

  }

  private def rollbackRepositories(username: String, token: String) = {
    rollbackAuthRepository(username, token)(_)
    rollbackUserRepository(username)(_)
  }

  /* Rollback changes previously performed in Authentication service */
  private def rollbackAuthRepository(username: String, token: String)(subscriber: Subscriber[Unit]) = {
    authRepository.deleteUser(DeleteUserRequest(username, token)).subscribe(subscriber)
  }

  /* Rollback changes previously performed in User service */
  private def rollbackUserRepository(username: String)(subscriber: Subscriber[Unit]): Unit = {
    userRepository.deleteUser(username).subscribe(subscriber)
  }
}

object RegistrationSubscriber {
  def apply(routingContext: RoutingContext,
            userRepository: UserRepository,
            authRepository: AuthenticationRepository)(implicit ctx: Context): RegistrationSubscriber =
    new RegistrationSubscriber(routingContext, authRepository, userRepository, ctx)
}
