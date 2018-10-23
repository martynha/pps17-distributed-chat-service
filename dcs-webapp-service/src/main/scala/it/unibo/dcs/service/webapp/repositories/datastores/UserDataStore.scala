package it.unibo.dcs.service.webapp.repositories.datastores


import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.UserDataStoreNetwork
import rx.lang.scala.Observable

/** Structure that allows access to user data by different means (e.g. network, file, database, ecc) */
trait UserDataStore {

  def deleteUser(username: String): Observable[String]


  /** Fetch the user with the given username
    *
    * @param username username provided to uniquely identify a user
    * @return an observable stream of just the user retrieved   */
  def getUserByUsername(username: String): Observable[User]

  /** Create a new user with the info passed in the request
    *
    * @param request the user registration request
    * @param token   token previously obtained by the Authentication service
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
