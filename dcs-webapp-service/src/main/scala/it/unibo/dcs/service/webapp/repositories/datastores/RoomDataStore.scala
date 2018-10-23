package it.unibo.dcs.service.webapp.repositories.datastores

import it.unibo.dcs.service.webapp.interaction.Requests
import it.unibo.dcs.service.webapp.interaction.Requests.{CreateRoomRequest, DeleteRoomRequest}
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.RoomDataStoreNetwork
import rx.lang.scala.Observable

/** Structure that allows access to rooms data by different means (e.g. network, file, database, ecc) */
trait RoomDataStore {

  def registerUser(request: Requests.RegisterUserRequest): Observable[Unit]

  def deleteRoom(request: DeleteRoomRequest): Observable[String]

  /** Store a new room given its information (e.g. room name)
    *
    * @param request needed data to store a room
    * @return an observable stream of just the created room */
  def createRoom(request: CreateRoomRequest): Observable[Room]
}

/** Companion object */
object RoomDataStore {

  /** Factory method to create a room data store that access data via network
    *
    * @param roomApi APIs to concat the room service
    * @return the RoomDataStore instance */
  def roomDataStoreNetwork(roomApi: RoomApi): RoomDataStore = new RoomDataStoreNetwork(roomApi)
}