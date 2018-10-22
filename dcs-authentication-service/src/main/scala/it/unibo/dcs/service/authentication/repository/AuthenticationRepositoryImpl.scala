package it.unibo.dcs.service.authentication.repository

import java.util.Date

import it.unibo.dcs.service.authentication.data.AuthenticationDataStore
import rx.lang.scala.Observable

class AuthenticationRepositoryImpl(private[this] val authDataStore: AuthenticationDataStore)
  extends AuthenticationRepository {

  override def createUser(username: String, password: String): Observable[Unit] =
    authDataStore.createUser(username, password)

  override def checkUserExistence(username: String): Observable[Unit] =
    authDataStore.checkUserExistence(username)

  override def checkUserCredentials(username: String, password: String): Observable[Unit] =
    authDataStore.checkUserCredentials(username, password)

  override def invalidToken(token: String, tokenExpirationDate: Date): Observable[Unit] =
    authDataStore.invalidToken(token, tokenExpirationDate)

  override def isTokenValid(token: String): Observable[Boolean] = authDataStore.isTokenValid(token)
}

object AuthenticationRepositoryImpl {
  def apply(authDataStore: AuthenticationDataStore) = new AuthenticationRepositoryImpl(authDataStore)
}
