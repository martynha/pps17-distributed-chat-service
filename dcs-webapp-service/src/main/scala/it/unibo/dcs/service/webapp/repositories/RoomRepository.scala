package it.unibo.dcs.service.webapp.repositories

import it.unibo.dcs.service.webapp.interaction.Requests.{CreateRoomRequest, DeleteRoomRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.datastores.RoomDataStore
import it.unibo.dcs.service.webapp.repositories.impl.RoomRepositoryImpl
import rx.lang.scala.Observable

/** Structure that handles Rooms data access and storage. */
trait RoomRepository {

  def registerUser(request: RegisterUserRequest): Observable[Unit]

  /** @param request
    * Needed data to create a new room *
    * @return an observable stream composed by the created room. */
  def createRoom(request: CreateRoomRequest): Observable[Room]

  def deleteRoom(request: DeleteRoomRequest): Observable[String]
}

/** Companion object */
object RoomRepository {

  /** Factory method to create the room repository
    *
    * @param roomDataStore room data store reference
    * @return the RoomRepository instance */
  def apply(roomDataStore: RoomDataStore): RoomRepository = new RoomRepositoryImpl(roomDataStore)
}

