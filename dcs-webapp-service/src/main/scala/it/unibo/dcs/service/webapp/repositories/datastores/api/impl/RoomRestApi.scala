package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.lang.scala.json.JsonObject
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{InternalException, RoomServiceErrorException, bodyAsJsonObject}
import it.unibo.dcs.service.webapp.interaction.Requests
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.RoomRestApi.getRoom
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

  override def registerUser(userRegistrationRequest: Requests.RegisterUserRequest): Observable[Unit] = {
    makeRequest(client =>
        Observable.from(client.post(RoomRestApi.createUser).sendJsonObjectFuture(userRegistrationRequest)))
      .map(bodyAsJsonObject())
      .map(_ => {})
      .onErrorResumeNext(cause => Observable.error(RoomServiceErrorException(cause)))
  }

}

private[impl] object RoomRestApi {

  val createRoomURI = "/createRoom"

  val deleteRoomURI = "/deleteRoom"

  val createUser = "/createUser"

  private[impl] def getRoom(json: JsonObject) = {
    Room(json.getString("name"))
  }

}
