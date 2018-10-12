package it.unibo.dcs.service.webapp.repositories.datastores

import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.interaction.Requests.CreateRoomRequest
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.RoomDataStoreNetwork
import rx.lang.scala.Observable

trait RoomDataStore {
  def createRoom(request: CreateRoomRequest): Observable[Room]
}

object RoomDataStore {
  def roomDataStoreNetwork(roomApi: RoomApi): RoomDataStore = new RoomDataStoreNetwork(roomApi)
}