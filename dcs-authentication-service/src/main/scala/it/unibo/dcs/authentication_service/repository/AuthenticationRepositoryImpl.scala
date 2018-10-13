package it.unibo.dcs.authentication_service.repository

import java.util.Date

import it.unibo.dcs.authentication_service.data.AuthenticationDataStore
import rx.lang.scala.Observable

class AuthenticationRepositoryImpl(private[this] val authDataStore: AuthenticationDataStore)
  extends AuthenticationRepository {

  override def createUser(username: String, password: String): Observable[Unit] =
    authDataStore.createUser(username, password)

  override def loginUser(username: String, password: String): Observable[Unit] =
    authDataStore.checkUserExistence(username, password)

  override def invalidToken(token: String, tokenExpirationDate: Date): Observable[Unit] =
    authDataStore.invalidToken(token, tokenExpirationDate)

  override def isTokenInvalid(token: String): Observable[Boolean] = authDataStore.isTokenInvalid(token)
}
