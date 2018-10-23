package it.unibo.dcs.service.webapp.repositories

import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.datastores.UserDataStore
import it.unibo.dcs.service.webapp.repositories.impl.UserRepositoryImpl
import rx.lang.scala.Observable

/** Structure that handles User data access and storage. */
trait UserRepository {

  def deleteUser(username: String): Observable[String]

  /** Retrieve a user given its username
    *
    * @param username username
    * @return an observable stream composed by the retrieved user */
  def getUserByUsername(username: String): Observable[User]

  /** Register a new user given its information
    *
    * @param request
    * Needed information to register a new user
    * @return an observable stream composed by the created user */
  def registerUser(request: RegisterUserRequest): Observable[User]

}

/** Companion object */
object UserRepository {

  /** Factory method to create the user repository
    *
    * @param userDataStore user data store reference
    * @return the UserRepository instance */
  def apply(userDataStore: UserDataStore): UserRepository = new UserRepositoryImpl(userDataStore)
}
