package it.unibo.dcs.service.webapp.interaction

import com.google.gson.Gson
import io.vertx.lang.scala.json.{Json, JsonObject}
import it.unibo.dcs.commons.dataaccess.Implicits.stringToDate
import it.unibo.dcs.service.webapp.model.{Room, User}

import scala.language.implicitConversions

/** It wraps all requests used by request handler, use cases, repositories, datastores and APIs */
object Requests {

  /** Sum type representing all the specific requests for Distributed Chat Service application */
  sealed trait DcsRequest

  final case class LoginUserRequest(username: String, password: String) extends DcsRequest

  final case class DeleteUserRequest(username: String, token: String) extends DcsRequest

  final case class RegisterUserRequest(username: String, firstName: String,
                                       lastName: String, password: String,
                                       passwordConfirm: String) extends DcsRequest

  final case class LogoutUserRequest(username: String, token: String) extends DcsRequest

  final case class CreateRoomRequest(name: String, username: String, token: String) extends DcsRequest

  final case class DeleteRoomRequest(name: String, username: String, token: String) extends DcsRequest

  final case class RoomJoinRequest(name: String, username: String, token: String) extends DcsRequest

  final case class CheckTokenRequest(token: String) extends DcsRequest

  /** It enables implicit conversions in order to clean code that deals with requests. */
  object Implicits {

    private val gson = new Gson()

    implicit def requestToJson(request: DcsRequest): JsonObject = Json.fromObjectString(gson.toJson(request))

    implicit def jsonToDeleteRoomRequest(json: JsonObject): DeleteRoomRequest = {
      DeleteRoomRequest(json.getString("name"), json.getString("username"), json.getString("token"))
    }

    implicit def jsonToCheckTokenRequest(json: JsonObject): CheckTokenRequest = {
      CheckTokenRequest(json.getString("token"))
    }

    implicit def jsonObjectToRegisterUserRequest(json: JsonObject): RegisterUserRequest = {
      RegisterUserRequest(json.getString("username"), json.getString("firstName"),
        json.getString("lastName"), json.getString("password"), json.getString("passwordConfirm"))
    }

    implicit def jsonObjectToUsername(json: JsonObject): String = {
      json.getString("username")
    }

    implicit def jsonObjectToLoginUserRequest(json: JsonObject): LoginUserRequest = {
      LoginUserRequest(json.getString("username"), json.getString("password"))
    }

    implicit def jsonObjectToLogoutUserRequest(json: JsonObject): LogoutUserRequest = {
      LogoutUserRequest(json.getString("username"), json.getString("authentication"))
    }

    implicit def jsonObjectToUser(json: JsonObject): User = {
      User(json.getString("username"), json.getString("firstName"),
        json.getString("lastName"), json.getString("bio"), json.getBoolean("visible"),
        json.getString("lastSeen"))
    }

    implicit def jsonObjectToRoom(json: JsonObject): Room = {
      Room(json.getString("name"))
    }

    implicit def jsonObjectToCreateRoomRequest(json: JsonObject): CreateRoomRequest = {
      CreateRoomRequest(json.getString("name"), json.getString("username"),
        json.getString("token"))
    }

    implicit def jsonObjectToRoomJoinRequest(json: JsonObject): RoomJoinRequest = {
      RoomJoinRequest(json.getString("name"), json.getString("username"), json.getString("token"))
    }
  }

}
