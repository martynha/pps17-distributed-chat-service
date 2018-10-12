package it.unibo.dcs.service.webapp.repositories

import it.unibo.dcs.service.webapp.interaction.Requests.{CreateRoomRequest, LoginUserRequest, LogoutUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore
import it.unibo.dcs.service.webapp.repositories.impl.AuthenticationRepositoryImpl
import rx.lang.scala.Observable

trait AuthenticationRepository {
  def loginUser(request: LoginUserRequest): Observable[String]

  def registerUser(request: RegisterUserRequest): Observable[String]

  def logoutUser(request: LogoutUserRequest): Observable[Unit]

  def createRoom(request: CreateRoomRequest): Observable[Unit]
}

object AuthenticationRepository {
  def apply(authenticationDataStore: AuthenticationDataStore): AuthenticationRepository =
    new AuthenticationRepositoryImpl(authenticationDataStore)
}
