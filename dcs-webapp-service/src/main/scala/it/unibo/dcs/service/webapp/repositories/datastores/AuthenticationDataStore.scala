package it.unibo.dcs.service.webapp.repositories.datastores

import it.unibo.dcs.service.webapp.interaction.Requests.{CreateRoomRequest, LoginUserRequest, LogoutUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.repositories.datastores.api.AuthenticationApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.AuthenticationDataStoreNetwork
import rx.lang.scala.Observable

trait AuthenticationDataStore {

  def loginUser(request: LoginUserRequest): Observable[String]

  def registerUser(request: RegisterUserRequest): Observable[String]

  def logoutUser(request: LogoutUserRequest): Observable[Unit]

  def createRoom(request: CreateRoomRequest): Observable[Unit]
}

/* Companion object */
object AuthenticationDataStore {

  /* Factory method */
  def authDataStoreNetwork(authApi: AuthenticationApi): AuthenticationDataStore =
    new AuthenticationDataStoreNetwork(authApi)
}
