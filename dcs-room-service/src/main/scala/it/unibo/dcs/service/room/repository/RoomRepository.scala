package it.unibo.dcs.service.room.repository

import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest, GetRoomRequest}
import rx.lang.scala.Observable

trait RoomRepository {

  def createUser(request: CreateUserRequest): Observable[Unit]

  def createRoom(request: CreateRoomRequest): Observable[Room]

  def deleteRoom(request: DeleteRoomRequest): Observable[String]

  def getRoomByName(request: GetRoomRequest): Observable[Room]

}
