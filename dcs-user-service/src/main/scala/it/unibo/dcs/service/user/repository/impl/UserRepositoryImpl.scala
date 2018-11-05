package it.unibo.dcs.service.user.repository.impl

import it.unibo.dcs.service.user.data.UserDataStore
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.{CreateUserRequest, EditUserRequest, GetUserRequest}
import rx.lang.scala.Observable

final class UserRepositoryImpl(private[this] val userDataStore: UserDataStore) extends UserRepository {

  override def checkIfUserExists(request: GetUserRequest): Observable[Unit] = userDataStore.checkIfUserExists(request)

  override def createUser(request: CreateUserRequest): Observable[User] = userDataStore.createUser(request)

  override def editUser(request: EditUserRequest): Observable[User] = userDataStore.editUser(request)

  override def getUserByUsername(request: GetUserRequest): Observable[User] = userDataStore.getUserByUsername(request)
}
