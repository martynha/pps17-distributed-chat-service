package it.unibo.dcs.service.user.repository

import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.request.{CreateUserRequest, EditUserRequest, GetUserRequest}
import rx.lang.scala.Observable

trait UserRepository {

  def checkIfUserExists(request: GetUserRequest): Observable[Unit]

  def createUser(request: CreateUserRequest): Observable[User]

  def editUser(request: EditUserRequest): Observable[User]

  def getUserByUsername(request: GetUserRequest): Observable[User]

}
