package it.unibo.dcs.service.webapp.repositories.impl

import it.unibo.dcs.service.webapp.interaction.Requests.CreateRoomRequest
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.RoomRepository
import it.unibo.dcs.service.webapp.repositories.datastores.RoomDataStore
import rx.lang.scala.Observable

class RoomRepositoryImpl(roomDataStore: RoomDataStore) extends RoomRepository {
  override def createRoom(request: CreateRoomRequest): Observable[Room] = roomDataStore.createRoom(request)
}
