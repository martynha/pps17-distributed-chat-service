package it.unibo.dcs.service.webapp.interaction

import com.google.gson.Gson
import io.vertx.lang.scala.json.Json
import it.unibo.dcs.service.webapp.model.{Room, User}

import scala.language.implicitConversions

object Results {

  final case class LoginResult(loggedUser: User, token: String)

  final case class RegisterResult(registeredUser: User, token: String)

  final case class RoomCreationResult(createdRoom: Room)

  object Implicits {

    implicit def registrationResultToJsonString(result: RegisterResult): String = {
      val registeredUserJson = new Gson().toJson(result.registeredUser)
      Json.obj(("token", result.token), ("user", registeredUserJson)).encodePrettily()
    }

    implicit def loginResultToJsonString(result: LoginResult): String = {
      val loggedUserJson = new Gson().toJson(result.loggedUser)
      Json.obj(("token", result.token), ("user", loggedUserJson)).encodePrettily()
    }

    implicit def roomCreationResultToJsonString(result: RoomCreationResult): String = {
      val createdRoomJson = new Gson().toJson(result)
      Json.obj(("room", createdRoomJson)).encodePrettily()
    }
  }

}
