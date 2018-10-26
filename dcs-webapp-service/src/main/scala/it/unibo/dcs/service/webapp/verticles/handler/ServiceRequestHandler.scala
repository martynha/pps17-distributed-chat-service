package it.unibo.dcs.service.webapp.verticles.handler

import io.vertx.core.Vertx
import io.vertx.scala.core.Context
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore.authDataStoreNetwork
import it.unibo.dcs.service.webapp.repositories.datastores.RoomDataStore.roomDataStoreNetwork
import it.unibo.dcs.service.webapp.repositories.datastores.UserDataStore.userDataStoreNetwork
import it.unibo.dcs.service.webapp.repositories.datastores.api.AuthenticationApi.authRestApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi.roomRestApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi.userRestApi
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository, UserRepository}
import it.unibo.dcs.service.webapp.verticles.handler.impl.ServiceRequestHandlerImpl

/** It encapsulates the business logic of the service.
  * It handles all the incoming request to the service APIs. */
trait ServiceRequestHandler {

  /** Join a user in a specified room
    *
    * @param context Vertx routing context
    * @param ctx     Vertx context passed implicitly
    */
  def handleJoinRoom(context: RoutingContext)(implicit ctx: Context): Unit

  /** Login request handler
    *
    * @param context Vertx routing context
    * @param ctx     Vertx context passed implicitly */
  def handleLogin(context: RoutingContext)(implicit ctx: Context): Unit

  /** User registration request handler
    *
    * @param context Vertx routing context
    * @param ctx     Vertx context passed implicitly */
  def handleRegistration(context: RoutingContext)(implicit ctx: Context): Unit

  /** User logout request handler
    *
    * @param context Vertx routing context
    * @param ctx     Vertx context passed implicitly */
  def handleLogout(context: RoutingContext)(implicit ctx: Context): Unit

  def handleRoomDeletion(context: RoutingContext)(implicit ctx: Context): Unit

  /** Room creation request handler
    *
    * @param context Vertx routing context
    * @param ctx     Vertx context passed implicitly */
  def handleRoomCreation(context: RoutingContext)(implicit ctx: Context): Unit
}

/** Companion object */
object ServiceRequestHandler {

  /** Factory method to create the request handler
    *
    * @param vertx    Vertx instance
    * @param eventBus Vertx Event Bus
    * @return the ServiceRequestHandler object */
  def apply(vertx: Vertx, eventBus: EventBus): ServiceRequestHandler = {
    val userApi = userRestApi(vertx, eventBus)
    val authApi = authRestApi(vertx, eventBus)
    val roomApi = roomRestApi(vertx, eventBus)
    val userDataStore = userDataStoreNetwork(userApi)
    val authDataStore = authDataStoreNetwork(authApi)
    val roomDataStore = roomDataStoreNetwork(roomApi)
    val userRepository = UserRepository(userDataStore)
    val authRepository = AuthenticationRepository(authDataStore)
    val roomRepository = RoomRepository(roomDataStore)
    new ServiceRequestHandlerImpl(eventBus, userRepository, authRepository, roomRepository)
  }

}
