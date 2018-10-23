package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{InternalException, UserServiceErrorException, bodyAsJsonObject}
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests.{Implicits, RegisterUserRequest}
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.UserRestApi._
import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global

class UserRestApi(private[this] val discovery: HttpEndpointDiscovery)
  extends AbstractApi(discovery, "user-service") with UserApi {

  override def createUser(registrationRequest: RegisterUserRequest): Observable[User] = {
    makeRequest(client =>
      Observable.from(client.post(createUserURI).sendJsonObjectFuture(registrationRequest)))
      .map(bodyAsJsonObject(throw InternalException("User service returned an empty body")))
      .map(jsonObjectToUser)
      .onErrorResumeNext(cause => Observable.error(UserServiceErrorException(cause)))
  }

  override def getUserByUsername(username: String): Observable[User] =
    makeRequest(client =>
      Observable.from(client.get(getUserURI(username)).sendFuture()))
      .map(bodyAsJsonObject(throw InternalException("User service returned an empty body")))
      .map(jsonObjectToUser)

  override def deleteUser(username: String): Observable[String] = makeRequest(client =>
    Observable.from(client.get(deleteUserURI(username)).sendFuture()))
    .map(bodyAsJsonObject(throw InternalException("User service returned an empty body")))
    .map(_.getString("username"))
    .onErrorResumeNext(cause => Observable.error(UserServiceErrorException(cause)))
}

private[impl] object UserRestApi {

  val createUserURI = "/createUser"
  val validateRegistration = "/validateRegistration"

  def deleteUserURI(username: String) = s"/deleteUser/$username"

  def getUserURI(username: String) = s"/getUser/$username"
}
