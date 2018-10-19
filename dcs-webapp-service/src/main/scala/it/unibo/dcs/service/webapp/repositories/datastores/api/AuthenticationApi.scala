package it.unibo.dcs.service.webapp.repositories.datastores.api

import io.vertx.core.Vertx
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.servicediscovery.ServiceDiscovery
import it.unibo.dcs.commons.service.HttpEndpointDiscoveryImpl
import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.AuthenticationRestApi
import rx.lang.scala.Observable

/** Utility wrapper for making requests to the Authentication Service via the network */
trait AuthenticationApi {

  /** It tells the Authentication Service to check if the room creation is allowed
    *
    * @param request info about room creation
    * @return an empty observable */
  def createRoom(request: CreateRoomRequest): Observable[Unit]

  /** It tells the Authentication Service to check the validity of the login credentials
    *
    * @param request user login credentials
    * @return an observable stream just of the created token */
  def loginUser(request: LoginUserRequest): Observable[String]

  /** It tells the Authentication Service to check registration info and create a new token
    *
    * @param request registration info
    * @return an observable stream just of the created token */
  def registerUser(request: RegisterUserRequest): Observable[String]

  /** It tell the Authentication Service to check the token and invalidate it
    *
    * @param request logout information and token
    * @return an empty observable */
  def logoutUser(request: LogoutUserRequest): Observable[Unit]

  /** It tell the Authentication Service to check that the token is valid
    *
    * @param request request information containing the token
    * @return an empty observable */
  def checkToken(request: CheckTokenRequest): Observable[Unit]
}

/** Companion object */
object AuthenticationApi {

  /** Factory method to create a rest api that communicates with the authentication service */
  def authRestApi(vertx: Vertx, eventBus: EventBus): AuthenticationApi = new AuthenticationRestApi(
    new HttpEndpointDiscoveryImpl(ServiceDiscovery.create(vertx), eventBus))
}
