package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.scala.ext.web.client.WebClient
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.service.webapp.interaction.Requests.CreateRoomRequest
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.exceptions.RoomCreationException
import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global


class RoomRestApi(private[this] val discovery: HttpEndpointDiscovery)
  extends AbstractApi(discovery, "RoomService") with RoomApi {

  override def createRoom(createRoomRequest: CreateRoomRequest): Observable[Room] = {
    for {
      response <- request((roomWebClient: WebClient) =>
        Observable.from(roomWebClient.post("/api/room/").sendJsonObjectFuture(createRoomRequest)))
    } yield response.bodyAsJsonObject().getOrElse(throw RoomCreationException())
  }
}
