package it.unibo.dcs.service.webapp.interaction

import com.google.gson.Gson
import io.vertx.lang.scala.json.Json
import it.unibo.dcs.service.webapp.model.{Room, User}

import scala.language.implicitConversions

/** It wraps all results produces by the use cases executions. */
object Results {

  final case class LoginResult(loggedUser: User, token: String)

  final case class RegisterResult(registeredUser: User, token: String)

  final case class RoomCreationResult(createdRoom: Room)


  /** It enables implicit conversions in order to clean code that deal with results. */
  object Implicits {

    private val gsonInstance = new Gson()

    implicit def registrationResultToJsonString(result: RegisterResult): String = {
      val registeredUserJson = gsonInstance.toJson(result.registeredUser)
      Json.obj(("token", result.token), ("user", registeredUserJson)).encodePrettily()
    }

    implicit def loginResultToJsonString(result: LoginResult): String = {
      val loggedUserJson = gsonInstance.toJson(result.loggedUser)
      Json.obj(("token", result.token), ("user", loggedUserJson)).encodePrettily()
    }

    implicit def roomCreationResultToJsonString(result: RoomCreationResult): String = {
      val createdRoomJson = gsonInstance.toJson(result)
      Json.obj(("room", createdRoomJson)).encodePrettily()
    }
  }

}
