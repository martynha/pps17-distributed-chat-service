package it.unibo.dcs.service.webapp.interaction

import java.util.Date

import com.google.gson.Gson
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson
import it.unibo.dcs.commons.dataaccess.Implicits.stringToDate
import it.unibo.dcs.service.webapp.interaction.Labels.JsonLabels._
import it.unibo.dcs.service.webapp.model.{Participation, Room, User}
import net.liftweb.json._

import scala.language.implicitConversions

/** It wraps all requests used by request handler, use cases, it.unibo.dcs.service.webapp.repositories,
  * datastores and APIs */
object Requests {

  /** Sum type representing all the specific requests for Distributed Chat Service application */
  sealed trait DcsRequest

  final case class LoginUserRequest(username: String, password: String) extends DcsRequest

  final case class DeleteUserRequest(username: String, token: String) extends DcsRequest

  final case class RegisterUserRequest(username: String, firstName: String,
                                       lastName: String, password: String,
                                       passwordConfirm: String) extends DcsRequest

  final case class EditUserRequest(username: String, firstName: String, lastName: String, bio: String,
                                   visible: Boolean, token: String) extends DcsRequest

  final case class LogoutUserRequest(username: String, token: String) extends DcsRequest

  final case class CreateRoomRequest(name: String, username: String, token: String) extends DcsRequest

  final case class DeleteRoomRequest(name: String, username: String, token: String) extends DcsRequest

  final case class RoomJoinRequest(name: String, username: String, token: String) extends DcsRequest

  final case class RoomLeaveRequest(name: String, username: String, token: String) extends DcsRequest

  final case class GetRoomsRequest(username: String, token: String) extends DcsRequest

  final case class GetRoomParticipationsRequest(name: String, username: String, token: String) extends DcsRequest

  final case class CheckTokenRequest(token: String, username: String) extends DcsRequest

  final case class SendMessageRequest(name: String, username: String, content: String, timestamp: Date, token: String) extends DcsRequest

  final case class GetUserParticipationsRequest(username: String, token: String) extends DcsRequest

  final case class GetUserRequest(username: String, token: String) extends DcsRequest

  /** It enables implicit conversions in order to clean code that deals with requests. */
  object Implicits {

    private val gson = new Gson()

    implicit def requestToJsonObject(request: DcsRequest): JsonObject = Json.fromObjectString(gson.toJson(request))

    implicit def jsonToDeleteRoomRequest(json: JsonObject): DeleteRoomRequest = {
      DeleteRoomRequest(json.getString(roomNameLabel), json.getString(usernameLabel), json.getString(tokenLabel))
    }

    implicit def jsonToCheckTokenRequest(json: JsonObject): CheckTokenRequest = {
      CheckTokenRequest(json.getString(tokenLabel), json.getString(usernameLabel))
    }

    implicit def jsonObjectToRegisterUserRequest(json: JsonObject): RegisterUserRequest = {
      RegisterUserRequest(json.getString(usernameLabel), json.getString(firstNameLabel),
        json.getString(lastNameLabel), json.getString(passwordLabel), json.getString(passwordConfirmLabel))
    }

    implicit def jsonObjectToEditUserRequest(json: JsonObject): EditUserRequest = {
      EditUserRequest(json.getString(usernameLabel), json.getString(firstNameLabel), json.getString(lastNameLabel),
        json.getString(bioLabel), json.getBoolean(visibleLabel), json.getString(tokenLabel))
    }

    implicit def jsonObjectToUsername(json: JsonObject): String = {
      json.getString(usernameLabel)
    }

    implicit def jsonObjectToLoginUserRequest(json: JsonObject): LoginUserRequest = {
      LoginUserRequest(json.getString(usernameLabel), json.getString(passwordLabel))
    }

    implicit def jsonObjectToLogoutUserRequest(json: JsonObject): LogoutUserRequest = {
      LogoutUserRequest(json.getString(usernameLabel), json.getString(authenticationLabel))
    }

    implicit def jsonObjectToUser(json: JsonObject): User = {
      User(json.getString(usernameLabel), json.getString(firstNameLabel),
        json.getString(lastNameLabel), json.getString(bioLabel), json.getBoolean(visibleLabel),
        json.getString(lastSeenLabel))
    }

    implicit def jsonObjectToRoom(json: JsonObject): Room = {
      Room(json.getString(roomNameLabel))
    }

    implicit def jsonArrayToParticipationSet(array: JsonArray): Set[Participation] = {
      implicit val formats: DefaultFormats.type = DefaultFormats
      val participations = parse(array.encode()).children.head.children
      participations.map(_.extract[Participation]).toSet
    }

    implicit def jsonObjectToCreateRoomRequest(json: JsonObject): CreateRoomRequest = {
      CreateRoomRequest(json.getString(roomNameLabel), json.getString(usernameLabel),
        json.getString(tokenLabel))
    }

    implicit def jsonObjectToRoomJoinRequest(json: JsonObject): RoomJoinRequest = {
      RoomJoinRequest(json.getString(roomNameLabel), json.getString(usernameLabel), json.getString(tokenLabel))
    }

    implicit def jsonObjectToRoomLeaveRequest(json: JsonObject): RoomLeaveRequest = {
      RoomLeaveRequest(json.getString(roomNameLabel), json.getString(usernameLabel), json.getString(tokenLabel))
    }

    implicit def jsonObjectToGetRoomsRequest(json: JsonObject): GetRoomsRequest = {
      GetRoomsRequest(json.getString(usernameLabel), json.getString(tokenLabel))
    }

    implicit def jsonObjectToSendMessageRequest(jsonObject: JsonObject): SendMessageRequest = {
      SendMessageRequest(jsonObject.getString(roomNameLabel), jsonObject.getString(usernameLabel),
        jsonObject.getString(messageContentLabel), jsonObject.getString(messageTimestampLabel), jsonObject.getString(tokenLabel))
    }

    implicit def jsonObjectToGetUserParticipationsRequest(json: JsonObject): GetUserParticipationsRequest = {
      gson fromJsonObject[GetUserParticipationsRequest] json
    }
  }

}
