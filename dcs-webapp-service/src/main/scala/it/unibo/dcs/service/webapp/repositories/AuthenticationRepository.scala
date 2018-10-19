package it.unibo.dcs.service.webapp.repositories

import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore
import it.unibo.dcs.service.webapp.repositories.impl.AuthenticationRepositoryImpl
import rx.lang.scala.Observable

/** Structure that handles Authentication data access and storage. */
trait AuthenticationRepository {

  /** @param request
    * User data needed to login
    * @return an observable stream of just one token */
  def loginUser(request: LoginUserRequest): Observable[String]

  /** @param request
    * Data inserted by the user to complete the registration
    * @return an observable stream of just one token */
  def registerUser(request: RegisterUserRequest): Observable[String]

  /** @param request
    * User data needed to logout
    * @return an empty observable object */
  def logoutUser(request: LogoutUserRequest): Observable[Unit]

  /** @param request
    * Data needed to create a new room
    * @return an empty observable object */
  def createRoom(request: CreateRoomRequest): Observable[Unit]

  /** @param request
    * Request containing the jwt token
    * @return an empty observable object */
  def checkToken(request: CheckTokenRequest): Observable[Unit]
}

/** Companion object */
object AuthenticationRepository {

  /** Factory method to create the authentication repository
    *
    * @param authenticationDataStore authentication data store reference
    * @return the AuthenticationRepository instance */
  def apply(authenticationDataStore: AuthenticationDataStore): AuthenticationRepository =
    new AuthenticationRepositoryImpl(authenticationDataStore)
}
