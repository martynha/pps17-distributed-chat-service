package it.unibo.dcs.service.webapp.repositories

import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.interaction.Requests.CreateRoomRequest
import it.unibo.dcs.service.webapp.repositories.datastores.RoomDataStore
import it.unibo.dcs.service.webapp.repositories.impl.RoomRepositoryImpl
import rx.lang.scala.Observable

trait RoomRepository {
  def createRoom(request: CreateRoomRequest): Observable[Room]
}

object RoomRepository {
  def apply(roomDataStore: RoomDataStore): RoomRepository = new RoomRepositoryImpl(roomDataStore)
}

