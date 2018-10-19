package it.unibo.dcs.service.user

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.dataaccess.Implicits.{booleanToString, dateToString}
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.model.exception.UserNotFoundException
import rx.lang.scala.Subscriber
import it.unibo.dcs.service.user.subscriber.Implicits._

package object subscriber {

  final class CreateUserSubscriber(private[this] val response: HttpServerResponse) extends Subscriber[User] {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onNext(user: User): Unit = {
      val json: JsonObject = user
      log.info(s"Answering with user: $json")
      response.end(json)
    }

    override def onCompleted(): Unit = ()

    override def onError(error: Throwable): Unit = error match {
      case UserNotFoundException(username) =>
        val statusJson: JsonObject = HttpResponseStatus.BAD_REQUEST
        val extras = new JsonObject().put("username", username)
        response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        response.end(new JsonObject().put("status", statusJson).put("extras", extras).toString)
    }

  }

  final class GetUserSubscriber(private[this] val response: HttpServerResponse) extends Subscriber[User] {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onNext(user: User): Unit = {
      val json: JsonObject = user
      log.info(s"Answering with user: $json")
      response.end(json)
    }

    override def onCompleted(): Unit = ()

    override def onError(error: Throwable): Unit = error match {
      case UserNotFoundException(username) =>
        val statusJson: JsonObject = HttpResponseStatus.NOT_FOUND
        val extras = new JsonObject().put("username", username)
        response.setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        response.write(new JsonObject().put("status", statusJson).put("extras", extras).toString)
    }

  }

  object Implicits {

    implicit def httpResponseStatusToJsonObject(status: HttpResponseStatus): JsonObject =
      new JsonObject().put("code", status.code()).put("reasonPhrase", status.reasonPhrase())

    implicit def userToJsonObject(user: User): JsonObject = {
      new JsonObject()
        .put("username", user.username)
        .put("firstName", user.firstName)
        .put("lastName", user.lastName)
        .put("bio", user.bio)
        .put("visible", booleanToString(user.visible))
        .put("lastSeen", dateToString(user.lastSeen))
    }

    implicit def jsonObjectToString(json: JsonObject): String = json.encode()

  }

}
