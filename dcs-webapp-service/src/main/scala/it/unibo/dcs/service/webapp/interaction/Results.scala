package it.unibo.dcs.service.webapp.interaction

import com.google.gson.Gson
import io.vertx.lang.scala.json.Json
import it.unibo.dcs.service.webapp.model.{Room, User}

import scala.language.implicitConversions

/** It wraps all results produces by the use cases executions. */
object Results {

  final case class LoginResult(user: User, token: String)

  final case class RegisterResult(user: User, token: String)

  final case class RoomCreationResult(room: Room)

  /** It enables implicit conversions in order to clean code that deal with results. */
  object Implicits {

    private val gsonInstance = new Gson()

    implicit def registrationResultToJsonString(result: RegisterResult): String = {
      val json = Json.fromObjectString(gsonInstance.toJson(result.user))
      json.put("token", result.token).encode()
    }

    implicit def loginResultToJsonString(result: LoginResult): String = {
      val json = Json.fromObjectString(gsonInstance.toJson(result.user))
      json.put("token", result.token).encode()
    }

    implicit def roomCreationResultToJsonString(result: RoomCreationResult): String = {
      val json = Json.fromObjectString(gsonInstance.toJson(result))
      json.encode()
    }
  }

}
