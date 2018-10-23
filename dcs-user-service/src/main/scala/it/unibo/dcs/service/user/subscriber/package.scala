package it.unibo.dcs.service.user

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.VertxWebHelper.Implicits.{RichHttpServerResponse, jsonObjectToString}
import it.unibo.dcs.commons.dataaccess.Implicits.dateToString
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.user.interactor.usecases.CreateUserUseCase
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.request.CreateUserRequest
import it.unibo.dcs.service.user.subscriber.Implicits._
import rx.lang.scala.Subscriber

import scala.language.implicitConversions

package object subscriber {

  final class CreateUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[User]
    with ErrorSubscriber {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onNext(user: User): Unit = {
      val json: JsonObject = user
      log.info(s"Answering with user: $json")
      response.setStatus(HttpResponseStatus.CREATED).end(json)
    }

  }

  final class ValidateUserCreationSubscriber(protected override val response: HttpServerResponse,
                                             private[this] val request: CreateUserRequest,
                                             private[this] val createUserUseCase: CreateUserUseCase) extends Subscriber[Unit]
      with ErrorSubscriber {

    override def onCompleted(): Unit = createUserUseCase(request, new CreateUserSubscriber(response))

  }

  final class GetUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[User]
    with ErrorSubscriber {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onNext(user: User): Unit = {
      val json: JsonObject = user
      log.info(s"Answering with user: $json")
      response.end(json)
    }

  }

  object Implicits {

    implicit def userToJsonObject(user: User): JsonObject = {
      new JsonObject()
        .put("username", user.username)
        .put("firstName", user.firstName)
        .put("lastName", user.lastName)
        .put("bio", user.bio)
        .put("visible", user.visible)
        .put("lastSeen", dateToString(user.lastSeen))
    }

  }

}