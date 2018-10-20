package it.unibo.dcs.service.user

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.VertxWebHelper.endErrorResponse
import it.unibo.dcs.commons.dataaccess.Implicits.{booleanToString, dateToString}
import it.unibo.dcs.commons.validation.ErrorTypes._
import it.unibo.dcs.exceptions.{MissingFirstNameException, MissingLastNameException, MissingUsernameException, UsernameAlreadyTaken}
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.model.exception.UserNotFoundException
import it.unibo.dcs.service.user.subscriber.Implicits._
import rx.lang.scala.Subscriber

import scala.language.implicitConversions

package object subscriber {

  final class CreateUserSubscriber(private[this] val response: HttpServerResponse) extends Subscriber[User] {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onNext(user: User): Unit = {
      val json: JsonObject = user
      log.info(s"Answering with user: $json")
      response.end(json)
    }
  }

  final class ValidateUserCreationSubscriber(private[this] val response: HttpServerResponse) extends Subscriber[Unit] {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onCompleted(): Unit = response.end()

    override def onError(error: Throwable): Unit = error match {
      case MissingUsernameException(message) =>
        log.error(error.getMessage)
        endErrorResponse(response, HttpResponseStatus.BAD_REQUEST, errorType = missingUsername, message)

      case MissingFirstNameException(message) =>
        log.error(error.getMessage)
        endErrorResponse(response, HttpResponseStatus.BAD_REQUEST, errorType = missingFirstName, message)

      case MissingLastNameException(message) =>
        log.error(error.getMessage)
        endErrorResponse(response, HttpResponseStatus.BAD_REQUEST, errorType = missingLastName, message)

      case UsernameAlreadyTaken(username) =>
        log.error(error.getMessage)
        endErrorResponse(response, HttpResponseStatus.BAD_REQUEST, errorType = usernameAlreadyTaken,
          description = "username: " + username + "is already taken")
    }
  }

  final class GetUserSubscriber(private[this] val response: HttpServerResponse) extends Subscriber[User] {

    private[this] val log = ScalaLogger.getLogger(getClass.getName)

    override def onNext(user: User): Unit = {
      val json: JsonObject = user
      log.info(s"Answering with user: $json")
      response.end(json)
    }

    override def onError(error: Throwable): Unit = error match {
      case UserNotFoundException(username) =>
        endErrorResponse(response, HttpResponseStatus.NOT_FOUND, errorType = userNotFound,
          description = "User with username: " + username + "not found")
    }

  }

  object Implicits {

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
