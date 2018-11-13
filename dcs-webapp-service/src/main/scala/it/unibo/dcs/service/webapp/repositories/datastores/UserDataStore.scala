package it.unibo.dcs.service.webapp.repositories.datastores


import it.unibo.dcs.service.webapp.interaction.Requests.{EditUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.UserDataStoreNetwork
import rx.lang.scala.Observable

/** Structure that allows access to user data by different means (e.g. network, file, database, ecc) */
trait UserDataStore {

  def updateAccess(username: String): Observable[Unit]


  /** Delete a user given its username
    *
    * @param username username of the user to delete
    * @return an observable stream of the deleted user
    */
  def deleteUser(username: String): Observable[String]

  /** Edit a user given its username
    *
    * @param request the user editing request
    * @return an observable stream of the edited user
    */
  def editUser(request: EditUserRequest): Observable[User]

  /** Fetch the user with the given username
    *
    * @param username username provided to uniquely identify a user
    * @return an observable stream of just the user retrieved   */
  def getUserByUsername(username: String): Observable[User]

  /** Create a new user with the info passed in the request
    *
    * @param request the user registration request
    * @return an observable stream of just the user created */
  def createUser(request: RegisterUserRequest): Observable[User]

}

/** Companion object */
object UserDataStore {

  /** Factory method to create a user data store that store/retrieve data via network
    *
    * @param userApi user api to contact the user service
    * @return the UserDataStoreNetwork instance */
  def userDataStoreNetwork(userApi: UserApi): UserDataStore = new UserDataStoreNetwork(userApi)
}
