package it.unibo.dcs.service.webapp.interaction

import io.vertx.lang.scala.json.{Json, JsonObject}
import it.unibo.dcs.commons.dataaccess.Implicits.stringToDate
import it.unibo.dcs.service.webapp.model.{Room, User}

import scala.language.implicitConversions

/** It wraps all requests used by request handler, use cases, repositories, datastores and APIs */
object Requests {

  final case class LoginUserRequest(username: String, password: String)

  final case class RegisterUserRequest(username: String, firstName: String, lastName: String, password: String, passwordConfirm: String)

  final case class LogoutUserRequest(username: String, token: String)

  final case class CreateRoomRequest(name: String, username: String, token: String)

  final case class DeleteRoomRequest(roomName: String, username: String, token: String)

  final case class CheckTokenRequest(token: String)

  /** It enables implicit conversions in order to clean code that deal with requests. */
  object Implicits {

    implicit def deleteRoomRequestToJson(request: DeleteRoomRequest): JsonObject = {
      Json.obj(("name", request.roomName), ("username", request.username), ("token", request.token))
    }

    implicit def jsonToDeleteRoomRequest(json: JsonObject): DeleteRoomRequest = {
      DeleteRoomRequest(json.getString("name"), json.getString("username"), json.getString("token"))
    }

    implicit def checkTokenRequestToJson(request: CheckTokenRequest): JsonObject = {
      Json.obj(("token", request.token))
    }

    implicit def jsonToCheckTokenRequest(json: JsonObject): CheckTokenRequest = {
      CheckTokenRequest(json.getString("token"))
    }

    implicit def createRoomRequestToJsonObject(request: CreateRoomRequest): JsonObject = {
      Json.obj(("name", request.name), ("username", request.username))
    }

    implicit def registerUserRequestToJsonObject(request: RegisterUserRequest): JsonObject = {
      Json.obj(("username", request.username), ("firstName", request.firstName),
        ("lastName", request.lastName), ("password", request.password),
        ("passwordConfirm", request.passwordConfirm))
    }

    implicit def jsonObjectToRegisterUserRequest(json: JsonObject): RegisterUserRequest = {
      RegisterUserRequest(json.getString("username"), json.getString("firstName"),
        json.getString("lastName"), json.getString("password"), json.getString("passwordConfirm"))
    }

    implicit def logoutUserRequestToJsonObject(request: LogoutUserRequest): JsonObject = {
      Json.obj(("username", request.username))
    }

    implicit def usernameToJsonObject(username: String): JsonObject = Json.obj(("username", username))

    implicit def jsonObjectToUsername(json: JsonObject): String = {
      json.getString("username")
    }

    implicit def loginUserRequestToJsonObject(request: LoginUserRequest): JsonObject = {
      Json.obj(("username", request.username), ("password", request.password))
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
  }

}
