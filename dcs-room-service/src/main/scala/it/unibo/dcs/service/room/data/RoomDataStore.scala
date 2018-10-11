package it.unibo.dcs.service.room.data

import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest}
import rx.lang.scala.Observable

trait RoomDataStore {

  def createUser(request: CreateUserRequest): Observable[Unit]

  def createRoom(request: CreateRoomRequest): Observable[Unit]

  def deleteRoom(request: DeleteRoomRequest): Observable[Unit]

}
