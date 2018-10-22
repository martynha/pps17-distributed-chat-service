package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.scala.ext.web.client.WebClient
import it.unibo.dcs.commons.VertxWebHelper
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{RoomCreationResponseException, RoomDeletionResponseException}
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.exceptions.RegistrationResponseException
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
    } yield Observable.just(response).map(response => if(!VertxWebHelper.isCodeSuccessful(response.statusCode())){
      throw RoomDeletionResponseException("Room service returned an error")
    })
  }

  override def registerUser(userRegistrationRequest: RegisterUserRequest): Observable[Unit] = {
    for {
      response <- request((roomWebClient: WebClient) =>
        Observable.from(roomWebClient.post(RoomRestApi.registerUser).sendJsonObjectFuture(userRegistrationRequest)))
    } yield response.bodyAsJsonObject().getOrElse(throw RegistrationResponseException())
  }
}

private[impl] object RoomRestApi {

  val createRoomURI = "/createRoom"

  val deleteRoomURI = "/deleteRoom"

  val registerUser = "/registerUser"

}


