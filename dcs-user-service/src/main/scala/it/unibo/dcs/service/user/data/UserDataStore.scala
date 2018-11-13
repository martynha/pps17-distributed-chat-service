package it.unibo.dcs.service.user.data

import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.request.{CreateUserRequest, EditUserRequest, GetUserRequest}
import rx.lang.scala.Observable

trait UserDataStore {

  def updateAccess(username: String): Observable[Unit]

  def checkIfUserExists(request: GetUserRequest): Observable[Unit]

  def getUserByUsername(request: GetUserRequest): Observable[User]

  def createUser(request: CreateUserRequest): Observable[User]

  def editUser(request: EditUserRequest): Observable[User]

}
