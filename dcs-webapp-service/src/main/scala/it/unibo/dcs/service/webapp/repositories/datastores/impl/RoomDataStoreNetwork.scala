package it.unibo.dcs.service.webapp.repositories.datastores.impl

import it.unibo.dcs.service.webapp.interaction.Requests
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.datastores.RoomDataStore
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import rx.lang.scala.Observable

class RoomDataStoreNetwork(private val roomApi: RoomApi) extends RoomDataStore {
  override def createRoom(request: Requests.CreateRoomRequest): Observable[Room] = roomApi.createRoom(request)
}
