package it.unibo.dcs.service.webapp.repositories.datastores.impl

import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore
import it.unibo.dcs.service.webapp.repositories.datastores.api.AuthenticationApi
import rx.lang.scala.Observable

class AuthenticationDataStoreNetwork(private val authenticationApi: AuthenticationApi) extends AuthenticationDataStore {

  override def loginUser(loginUserRequest: LoginUserRequest): Observable[String] =
    authenticationApi.loginUser(loginUserRequest)

  override def registerUser(request: RegisterUserRequest): Observable[String] =
    authenticationApi.registerUser(request)

  override def logoutUser(request: LogoutUserRequest): Observable[Unit] = authenticationApi.logoutUser(request)

  override def checkToken(request: CheckTokenRequest): Observable[Unit] =
    authenticationApi.checkToken(request)

  override def deleteUser(request: DeleteUserRequest): Observable[Unit] = authenticationApi.deleteUser(request)
}
