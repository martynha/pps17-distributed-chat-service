package it.unibo.dcs.service.user

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson
import it.unibo.dcs.commons.Logging
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.subscriber.Implicits._
import rx.lang.scala.Subscriber

import scala.language.implicitConversions

package object subscriber {

  final class CreateUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[User]
    with ErrorSubscriber with Logging {

    override def onNext(user: User): Unit = {
      log.debug(s"Answering with user: $user")
      response setStatus HttpResponseStatus.CREATED endWith user
    }

  }

  final class GetUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[User]
    with ErrorSubscriber with Logging {

    override def onNext(user: User): Unit = {
      log.debug(s"Answering with user: $user")
      response endWith user
    }

  }

  object Implicits {

    implicit def userToJsonObject(user: User): JsonObject = gson toJsonObject user

  }

}