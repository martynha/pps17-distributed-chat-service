package it.unibo.dcs.service.room.data

import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest, GetRoomRequest}
import rx.lang.scala.Observable

trait RoomDataStore {

  def createUser(request: CreateUserRequest): Observable[Unit]

  def getRoomByName(request: GetRoomRequest): Observable[Room]

  def createRoom(request: CreateRoomRequest): Observable[Room]

  def deleteRoom(request: DeleteRoomRequest): Observable[String]

}
