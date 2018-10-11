package it.unibo.dcs.service.room.repository

import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest}
import rx.lang.scala.Observable

trait RoomRepository {

  def createUser(request: CreateUserRequest): Observable[Unit]

  def createRoom(request: CreateRoomRequest): Observable[Unit]

  def deleteRoom(request: DeleteRoomRequest): Observable[Unit]

}
