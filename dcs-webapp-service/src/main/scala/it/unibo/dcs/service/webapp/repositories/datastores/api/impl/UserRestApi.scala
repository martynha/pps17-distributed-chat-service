package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.lang.scala.json.JsonObject
import it.unibo.dcs.commons.RxHelper.Implicits.RichObservable
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{InternalException, UserServiceErrorException, bodyAsJsonObject}
import it.unibo.dcs.service.webapp.interaction.Labels.JsonLabels
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.UserRestApi._
import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global

class UserRestApi(private[this] val discovery: HttpEndpointDiscovery)
  extends AbstractApi(discovery, "user-service") with UserApi {

  override def createUser(registrationRequest: RegisterUserRequest): Observable[User] = {
    makeRequest(client =>
      Observable.from(client.post(createUserURI)
        .sendJsonObjectFuture(toUserServiceRegistrationRequest(registrationRequest))))
      .map(bodyAsJsonObject(throw InternalException(emptyBodyErrorMessage)))
      .onErrorResumeNext(cause => Observable.error(UserServiceErrorException(cause)))
      .mapImplicitly
  }

  override def getUserByUsername(username: String): Observable[User] =
    makeRequest(client =>
      Observable.from(client.get(getUserURI(username)).sendFuture()))
      .map(bodyAsJsonObject(throw InternalException(emptyBodyErrorMessage)))
      .mapImplicitly

  override def deleteUser(username: String): Observable[String] = makeRequest(client =>
    Observable.from(client.get(deleteUserURI(username)).sendFuture()))
    .map(bodyAsJsonObject(throw InternalException(emptyBodyErrorMessage)))
    .onErrorResumeNext(cause => Observable.error(UserServiceErrorException(cause)))
    .map(_.getString(JsonLabels.usernameLabel))
}

private[impl] object UserRestApi {

  private val emptyBodyErrorMessage = "User service returned an empty body"

  private val createUserURI = "/users"

  private def deleteUserURI(username: String) = s"/users/$username"

  private def getUserURI(username: String) = s"/users/$username"

  private def toUserServiceRegistrationRequest(registerUserRequest: RegisterUserRequest): JsonObject = {
    val regRequest: JsonObject = registerUserRequest
    regRequest.remove(JsonLabels.passwordLabel)
    regRequest.remove(JsonLabels.passwordConfirmLabel)
    regRequest
  }
}
