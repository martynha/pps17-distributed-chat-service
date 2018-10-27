package it.unibo.dcs.service.webapp.repositories.datastores.api

import io.vertx.core.Vertx
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.servicediscovery.ServiceDiscovery
import it.unibo.dcs.commons.service.HttpEndpointDiscoveryImpl
import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.model.{Participation, Room}
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.RoomRestApi
import rx.lang.scala.Observable

/** Utility wrapper for making requests to the Room Service via the network */
trait RoomApi {

  /** It adds the user to the list of participants in the room
    *
    * @param request needed info to join a room
    * @return an observable stream of the new participation
    */
  def joinRoom(request: RoomJoinRequest): Observable[Participation]


  /** Register a new user given its info
    *
    * @param request needed info to register a new user
    * @return an empty observable
    */
  def registerUser(request: RegisterUserRequest): Observable[Unit]


  /** It tells the Room Service to store a new room
    *
    * @param request room to create information
    * @return an observable stream of just the created room */
  def createRoom(request: CreateRoomRequest): Observable[Room]

  /** Delete a room given its info
    *
    * @param request needed data to delete a room
    * @return an observable stream of the delete room's identifier
    */
  def deleteRoom(request: DeleteRoomRequest): Observable[String]

  /** It get the list of all rooms where the user has not yet joined
    *
    * @param request get rooms request
    * @return an observable stream of the list of rooms
    */
  def getRooms(request: GetRoomsRequest): Observable[List[Room]]
}

/** Companion object */
object RoomApi {

  /** Factory method to create a rest api that communicates with the Room Service
    *
    * @param vertx    Vertx instance
    * @param eventBus Vertx Event Bus
    * @return the RoomRestApi instance */
  def roomRestApi(vertx: Vertx, eventBus: EventBus): RoomApi =
    new RoomRestApi(new HttpEndpointDiscoveryImpl(ServiceDiscovery.create(vertx), eventBus))
}
