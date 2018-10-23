package it.unibo.dcs.service.room.repository.impl

import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest, GetRoomRequest}
import rx.lang.scala.Observable

final class RoomRepositoryImpl(private[this] val roomDataStore: RoomDataStore) extends RoomRepository {

  override def createUser(request: CreateUserRequest): Observable[Unit] = roomDataStore.createUser(request)

  override def deleteRoom(request: DeleteRoomRequest): Observable[String] = roomDataStore.deleteRoom(request)

  override def createRoom(request: CreateRoomRequest): Observable[Room] = roomDataStore.createRoom(request)

  override def getRoomByName(request: GetRoomRequest): Observable[Room] = roomDataStore.getRoomByName(request)
}
