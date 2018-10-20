package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.scala.ext.web.client.WebClient
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{RegistrationResponseException, RoomCreationResponseException, RoomDeletionResponseException}
import it.unibo.dcs.service.webapp.interaction.Requests
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests.{CreateRoomRequest, DeleteRoomRequest}
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global

class RoomRestApi(private[this] val discovery: HttpEndpointDiscovery)
  extends AbstractApi(discovery, "room-service") with RoomApi {

  override def createRoom(createRoomRequest: CreateRoomRequest): Observable[Room] = {
    for {
      response <- request((roomWebClient: WebClient) =>
        Observable.from(roomWebClient.post(RoomRestApi.createRoomURI).sendJsonObjectFuture(createRoomRequest)))
    } yield response.bodyAsJsonObject().getOrElse(throw RoomCreationResponseException("Room service returned an empty body"))
  }

  override def deleteRoom(deletionRequest: DeleteRoomRequest): Observable[Unit] = {
    for {
      response <- request((roomWebClient: WebClient) =>
        Observable.from(roomWebClient.post(RoomRestApi.deleteRoomURI).sendJsonObjectFuture(deletionRequest)))
    } yield response.bodyAsJsonObject().getOrElse(throw RoomDeletionResponseException("Room service returned an empty body"))
  }

  override def registerUser(userRegistrationRequest: Requests.RegisterUserRequest): Observable[Unit] = {
    for {
      response <- request((roomWebClient: WebClient) =>
        Observable.from(roomWebClient.post(RoomRestApi.registerUser).sendJsonObjectFuture(userRegistrationRequest)))
    } yield response.bodyAsJsonObject().getOrElse(throw RegistrationResponseException("Room service returned an empty body"))
  }
}

private[impl] object RoomRestApi {

  val createRoomURI = "/createRoom"

  val deleteRoomURI = "/deleteRoom"

  val registerUser = "/registerUser"

}
