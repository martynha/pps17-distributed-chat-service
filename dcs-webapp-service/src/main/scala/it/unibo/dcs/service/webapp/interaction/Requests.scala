package it.unibo.dcs.service.webapp.interaction

import java.util.Date

import io.vertx.lang.scala.json.{Json, JsonObject}
import it.unibo.dcs.service.webapp.model.{Room, User}

import scala.language.implicitConversions

/** It wraps all requests used by request handler, use cases, repositories, datastores and APIs */
object Requests {

  final case class LoginUserRequest(username: String, password: String)

  final case class RegisterUserRequest(username: String, password: String,
                                       firstName: String, lastName: String)

  final case class LogoutUserRequest(username: String, token: String)

  final case class CreateRoomRequest(roomName: String, creator: User, token: String)


  /** It enables implicit conversions in order to clean code that deal with requests. */
  object Implicits {

    implicit def createRoomRequestToJson(createRoomRequest: CreateRoomRequest): JsonObject = {
      Json.obj(("name", createRoomRequest.roomName), ("user", createRoomRequest.creator))
    }

    implicit def registerRequestToJson(registerRequest: RegisterUserRequest): JsonObject = {
      Json.obj(("username", registerRequest.firstName), ("lastname", registerRequest.lastName),
        ("password", registerRequest.password), ("username", registerRequest.username))
    }

    implicit def JsonToRegisterRequest(json: JsonObject): RegisterUserRequest = {
      RegisterUserRequest(json.getString("username"), json.getString("password"),
        json.getString("firstname"), json.getString("lastname"))
    }

    implicit def logoutRequestToJson(logoutUserRequest: LogoutUserRequest): JsonObject = {
      Json.obj(("username", logoutUserRequest.username))
    }

    implicit def usernameToJson(username: String): JsonObject = Json.obj(("username", username))

    implicit def jsonToUsername(json: JsonObject): String = {
      json.getString("username")
    }

    implicit def loginRequestToJson(loginUserRequest: LoginUserRequest): JsonObject = {
      Json.obj(("username", loginUserRequest.username), ("password", loginUserRequest.password))
    }

    implicit def jsonToLoginRequest(json: JsonObject): LoginUserRequest = {
      LoginUserRequest(json.getString("username"), json.getString("password"))
    }

    implicit def jsonToLogoutRequest(json: JsonObject): LogoutUserRequest = {
      LogoutUserRequest(json.getString("username"), json.getString("authentication"))
    }

    implicit def jsonToUser(userJson: JsonObject): User = {
      User(userJson.getString("username"), userJson.getString("firstname"),
        userJson.getString("lastname"), "", visible = true, new Date)
    }

    implicit def jsonToRoom(roomJson: JsonObject): Room = {
      Room(roomJson.getString("name"))
    }

    implicit def jsonToRoomCreationRequest(json: JsonObject): CreateRoomRequest = {
      CreateRoomRequest(json.getString("name"), json.getJsonObject("user"),
        json.getString("authorization"))
    }
  }

}
