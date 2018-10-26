package it.unibo.dcs.service.webapp.repositories.datastores.api

import io.vertx.core.Vertx
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.servicediscovery.ServiceDiscovery
import it.unibo.dcs.commons.service.HttpEndpointDiscoveryImpl
import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.UserRestApi
import rx.lang.scala.Observable

/** Utility wrapper for making requests to the User Service via the network */
trait UserApi {

  /** Delete a user given its username
    *
    * @param username username of the user to delete
    * @return an observable stream of the deleted user's username
    */
  def deleteUser(username: String): Observable[String]


  /** It tells user service to create a new user and returns it as Observable
    *
    * @param request user information needed to perform a registration
    * @return an observable stream of just the created user */
  def createUser(request: RegisterUserRequest): Observable[User]

  /** It request the user service to retrieve a user given its username
    *
    * @param username username
    * @return an observable stream of just the retrieved user */
  def getUserByUsername(username: String): Observable[User]
}

/** Companion object */
object UserApi {

  /** Factory method to create a rest api that communicates with the user service
    *
    * @param vertx    Vertx instance
    * @param eventBus Vertx Event Bus
    * @return the UserApi instance */
  def userRestApi(vertx: Vertx, eventBus: EventBus): UserApi = new UserRestApi(
    new HttpEndpointDiscoveryImpl(ServiceDiscovery.create(vertx), eventBus))
}
