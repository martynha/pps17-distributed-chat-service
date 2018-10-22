package it.unibo.dcs.service.webapp.repositories.datastores

import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.repositories.datastores.api.AuthenticationApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.AuthenticationDataStoreNetwork
import rx.lang.scala.Observable

/** Structure that allows access to authentication info by different means (e.g. network, file, database, etc) */
trait AuthenticationDataStore {

  def deleteUser(request: DeleteUserRequest): Observable[Unit]


  /**
    * It checks the credential validity and returns the token of the logged user
    *
    * @param request user login credentials
    * @return an observable stream of just the created token */
  def loginUser(request: LoginUserRequest): Observable[String]

  /**
    * It saves user info and generates a new token
    *
    * @param request user registration info
    * @return an observable stream of just the created token */
  def registerUser(request: RegisterUserRequest): Observable[String]

  /**
    * It performs user logout given its token
    *
    * @param request needed info to perform the logout operation
    * @return an empty observable stream */
  def logoutUser(request: LogoutUserRequest): Observable[Unit]

  /** It checks that the provided jwt token is valid
    *
    * @param request needed info to check the token validity
    * @return an empty observable stream */
  def checkToken(request: CheckTokenRequest): Observable[Unit]

}

/** Companion object */
object AuthenticationDataStore {

  /** Factory method to create an authentication data store that access data via network
    *
    * @param authApi authentication api to contact the Authentication service
    * @return the AuthenticationDataStore instance */
  def authDataStoreNetwork(authApi: AuthenticationApi): AuthenticationDataStore =
    new AuthenticationDataStoreNetwork(authApi)
}
