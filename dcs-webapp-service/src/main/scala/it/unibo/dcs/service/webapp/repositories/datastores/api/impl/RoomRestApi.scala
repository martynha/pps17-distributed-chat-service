package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.lang.scala.json.{JsonArray, JsonObject}
import it.unibo.dcs.commons.dataaccess.Implicits.stringToDate
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{InternalException, RoomServiceErrorException, bodyAsJsonArray, bodyAsJsonObject}
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.model.{Room, User}
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.RoomRestApi.{getRoom, getUser, getRoomList}
import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global

class RoomRestApi(private[this] val discovery: HttpEndpointDiscovery)
  extends AbstractApi(discovery, "room-service") with RoomApi {

  override def createRoom(createRoomRequest: CreateRoomRequest): Observable[Room] = {
    makeRequest(client =>
      Observable.from(client.post(RoomRestApi.createRoomURI).sendJsonObjectFuture(createRoomRequest)))
      .map(bodyAsJsonObject(throw InternalException("Room service returned an empty body")))
      .map(getRoom)
  }

  override def deleteRoom(deletionRequest: DeleteRoomRequest): Observable[String] = {
    makeRequest(client =>
      Observable.from(client.post(RoomRestApi.deleteRoomURI).sendJsonObjectFuture(deletionRequest)))
      .map(bodyAsJsonObject(throw InternalException("Room service returned an empty body")))
      .map(_.getString("name"))
  }

  override def registerUser(userRegistrationRequest: RegisterUserRequest): Observable[Unit] = {
    makeRequest(client =>
      Observable.from(client.post(RoomRestApi.createUser).sendJsonObjectFuture(userRegistrationRequest)))
      .map(bodyAsJsonObject())
      .map(_ => {})
      .onErrorResumeNext(cause => Observable.error(RoomServiceErrorException(cause)))
  }

  override def joinRoom(request: RoomJoinRequest): Observable[User] = {
    makeRequest(client =>
      Observable.from(client.post(RoomRestApi.joinRoomURI(request.name)).sendJsonObjectFuture(request)))
      .map(bodyAsJsonObject(throw InternalException("Room service returned an empty body")))
      .map(getUser)
  }

  override def getRooms(request: GetRoomsRequest): Observable[List[Room]] = {
    makeRequest(client =>
      Observable.from(client.get(RoomRestApi.getRooms).sendJsonObjectFuture(request)))
      .map(bodyAsJsonArray(throw InternalException("Room service returned an empty body")))
      .map(getRoomList)
  }
}

private[impl] object RoomRestApi {

  val createRoomURI = "/createRoom"

  val deleteRoomURI = "/deleteRoom"

  val createUser = "/createUser"

  val getRooms = "/rooms"

  private def joinRoomURI(roomName: String) = s"/joinRoom/$roomName"

  private[impl] def getRoom(json: JsonObject) = {
    Room(json.getString("name"))
  }

  private[impl] def getUser(json: JsonObject) = {
    User(json.getString("username"), json.getString("firstName"),
      json.getString("lastName"), json.getString("bio"), json.getBoolean("visible"),
      json.getString("lastSeen"))
  }

  private[impl] def getRoomList(jsonArray: JsonArray): List[Room] = {
    Stream.range(0, jsonArray.size)
      .map(jsonArray.getJsonObject)
      .map(_.getJsonObject("room"))
      .map(_.getString("name"))
      .map(Room)
      .toList
    /*
    val roomList: List[Room] = List()
    for (i <- 0 to jsonArray.size) {
      roomList :+ Room(jsonArray.getJsonObject(i).getString("name"))
    }
    roomList
    */
  }

}
