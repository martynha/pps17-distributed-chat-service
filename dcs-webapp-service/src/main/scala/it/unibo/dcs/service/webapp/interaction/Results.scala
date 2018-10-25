package it.unibo.dcs.service.webapp.interaction

import com.google.gson.Gson
import io.vertx.lang.scala.json.{Json, JsonObject}
import it.unibo.dcs.service.webapp.model.{Room, User}

import scala.language.implicitConversions

/** It wraps all results produced by the use cases executions. */
object Results {

  final case class LoginResult(user: User, token: String)

  final case class RegisterResult(user: User, token: String)

  final case class RoomCreationResult(room: Room)

  /** It enables implicit conversions in order to clean code that deals with results. */
  object Implicits {

    private val gsonInstance = new Gson()

    implicit def registrationResultToJsonObject(result: RegisterResult): JsonObject = {
      Json.fromObjectString(gsonInstance.toJson(result.user)).put("token", result.token)
    }

    implicit def loginResultToJsonObject(result: LoginResult): JsonObject = {
      Json.fromObjectString(gsonInstance.toJson(result.user)).put("token", result.token)
    }

    implicit def roomCreationResultToJsonObject(result: RoomCreationResult): JsonObject = {
      Json.fromObjectString(gsonInstance.toJson(result.room))
    }
  }

}
