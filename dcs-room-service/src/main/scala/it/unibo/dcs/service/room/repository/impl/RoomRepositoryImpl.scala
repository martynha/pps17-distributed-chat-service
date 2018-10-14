package it.unibo.dcs.service.room.repository.impl

import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest}
import rx.lang.scala.Observable

final class RoomRepositoryImpl(private[this] val roomDataStore: RoomDataStore) extends RoomRepository {

  override def createUser(request: CreateUserRequest): Observable[Unit] = roomDataStore.createUser(request)

  override def deleteRoom(request: DeleteRoomRequest): Observable[Unit] = roomDataStore.deleteRoom(request)

  override def createRoom(request: CreateRoomRequest): Observable[Unit] = roomDataStore.createRoom(request)

}
