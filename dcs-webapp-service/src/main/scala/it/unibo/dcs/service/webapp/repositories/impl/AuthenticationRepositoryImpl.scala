package it.unibo.dcs.service.webapp.repositories.impl

import it.unibo.dcs.service.webapp.interaction.Requests
import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.repositories.AuthenticationRepository
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore
import rx.lang.scala.Observable


class AuthenticationRepositoryImpl(private val authenticationDataStore: AuthenticationDataStore)
  extends AuthenticationRepository {

  override def loginUser(loginUserRequest: LoginUserRequest): Observable[String] =
    authenticationDataStore.loginUser(loginUserRequest)

  override def registerUser(request: RegisterUserRequest): Observable[String] =
    authenticationDataStore.registerUser(request)

  override def logoutUser(request: LogoutUserRequest): Observable[Unit] = authenticationDataStore.logoutUser(request)

  override def checkToken(request: CheckTokenRequest): Observable[Unit] =
    authenticationDataStore.checkToken(request)

  override def deleteUser(request: DeleteUserRequest): Observable[Unit] = authenticationDataStore.deleteUser(request)
}
