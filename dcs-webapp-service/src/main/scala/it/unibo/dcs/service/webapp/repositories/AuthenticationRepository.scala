package it.unibo.dcs.service.webapp.repositories

import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore
import it.unibo.dcs.service.webapp.repositories.impl.AuthenticationRepositoryImpl
import rx.lang.scala.Observable

/** Structure that handles Authentication data access and storage. */
trait AuthenticationRepository {

  /** Login a user given its credentials
    *
    * @param request User data needed to login
    * @return an observable stream of just one token */
  def loginUser(request: LoginUserRequest): Observable[String]

  /** Register a new user given its info
    *
    * @param request Data inserted by the user to complete the registration
    * @return an observable stream of just one token */
  def registerUser(request: RegisterUserRequest): Observable[String]

  /** Delete a user given its info
    *
    * @param request data needed to deleted a user
    * @return an empty observable
    */
  def deleteUser(request: DeleteUserRequest): Observable[Unit]

  /** Logout a user given its info
    *
    * @param request User data needed to logout
    * @return an empty observable object */
  def logoutUser(request: LogoutUserRequest): Observable[Unit]

  /** Check token validity given a request
    *
    * @param request Request containing the jwt token
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
