package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import it.unibo.dcs.commons.RxHelper.Implicits.RichObservable
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{InternalException, RoomServiceErrorException, bodyAsJsonArray, bodyAsJsonObject}
import it.unibo.dcs.service.webapp.gson
import it.unibo.dcs.service.webapp.interaction.Labels.JsonLabels
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.model.{Participation, Room}
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.RoomRestApi.Implicits._
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.RoomRestApi._
import rx.lang.scala.Observable

import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

class RoomRestApi(private[this] val discovery: HttpEndpointDiscovery)
  extends AbstractApi(discovery, "room-service") with RoomApi {

  override def createRoom(createRoomRequest: CreateRoomRequest): Observable[Room] = {
    makeRequest(client =>
      Observable.from(client.post(roomsURI).sendJsonObjectFuture(createRoomRequest)))
      .map(bodyAsJsonObject(throw InternalException(emptyBodyErrorMessage)))
      .mapImplicitly
  }

  override def deleteRoom(deletionRequest: DeleteRoomRequest): Observable[String] = {
    makeRequest(client =>
      Observable.from(client.delete(deleteRoomURI(deletionRequest.name))
        .sendJsonObjectFuture(toDeleteRoomRequest(deletionRequest))))
      .map(bodyAsJsonObject(throw InternalException(emptyBodyErrorMessage)))
      .map(_.getString(JsonLabels.roomNameLabel))
  }

  override def registerUser(userRegistrationRequest: RegisterUserRequest): Observable[Unit] = {
    makeRequest(client =>
      Observable.from(client.post(usersURI).sendJsonObjectFuture(toCreateUserRequest(userRegistrationRequest))))
      .map(bodyAsJsonObject())
      .onErrorResumeNext(cause => Observable.error(RoomServiceErrorException(cause)))
      .toCompletable
  }

  override def joinRoom(request: RoomJoinRequest): Observable[Participation] = {
    makeRequest(client =>
      Observable.from(client.post(joinRoomURI(request.name))
        .sendJsonObjectFuture(toJoinRoomRequest(request))))
      .map(bodyAsJsonObject(throw InternalException(emptyBodyErrorMessage)))
      .mapImplicitly
  }

  override def getRooms(request: GetRoomsRequest): Observable[List[Room]] = {
    makeRequest(client =>
      Observable.from(client.get(s"${RoomRestApi.roomsURI}?user=${request.username}").sendJsonObjectFuture(request)))
      .map(bodyAsJsonArray(throw InternalException(emptyBodyErrorMessage)))
      .mapImplicitly
  }
}

private[impl] object RoomRestApi {

  private val roomsURI = "/rooms"

  private val usersURI = "/users"

  private val emptyBodyErrorMessage = "Room service returned an empty body"

  private def joinRoomURI(roomName: String) = s"$roomsURI/$roomName/participations"

  private def deleteRoomURI(roomName: String) = roomsURI + "/" + roomName

  private def toDeleteRoomRequest(deleteRoomRequest: DeleteRoomRequest): JsonObject = {
    Json.obj((JsonLabels.usernameLabel, deleteRoomRequest.username))
  }

  private def toCreateUserRequest(registerUserRequest: RegisterUserRequest): JsonObject = {
    Json.obj((JsonLabels.usernameLabel, registerUserRequest.username))
  }

  private def toJoinRoomRequest(joinRoomRequest: RoomJoinRequest): JsonObject = {
    Json.obj((JsonLabels.usernameLabel, joinRoomRequest.username))
  }

  object Implicits {

    implicit def jsonObjectToParticipation(json: JsonObject): Participation = {
      println(json)
      gson fromJsonObject[Participation] json
    }

    implicit def jsonArrayToRooms(json: JsonArray): List[Room] = {
      Stream.range(0, json.size)
        .map(json.getJsonObject)
        .map(_.getString("name"))
        .map(Room)
        .toList
    }

  }

}
