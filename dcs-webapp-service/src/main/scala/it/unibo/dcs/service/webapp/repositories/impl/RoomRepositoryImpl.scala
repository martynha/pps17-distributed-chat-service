package it.unibo.dcs.service.webapp.repositories.impl

import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.model.{Participation, Room}
import it.unibo.dcs.service.webapp.repositories.RoomRepository
import it.unibo.dcs.service.webapp.repositories.datastores.RoomDataStore
import rx.lang.scala.Observable

class RoomRepositoryImpl(roomDataStore: RoomDataStore) extends RoomRepository {

  override def deleteRoom(request: DeleteRoomRequest): Observable[String] =
    roomDataStore.deleteRoom(request)

  override def createRoom(request: CreateRoomRequest): Observable[Room] = roomDataStore.createRoom(request)

  override def registerUser(request: RegisterUserRequest): Observable[Unit] =
    roomDataStore.registerUser(request)

  override def getRooms(request: GetRoomsRequest): Observable[List[Room]] = roomDataStore.getRooms(request)

  override def joinRoom(request: RoomJoinRequest): Observable[Participation] = roomDataStore.joinRoom(request)
}
