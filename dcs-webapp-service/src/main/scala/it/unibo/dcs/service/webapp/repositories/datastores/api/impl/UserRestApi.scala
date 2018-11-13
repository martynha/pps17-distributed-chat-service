package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.core.buffer.Buffer
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.web.client.HttpResponse
import it.unibo.dcs.commons.RxHelper.Implicits.RichObservable
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{InternalException, UserServiceErrorException, bodyAsJsonObject}
import it.unibo.dcs.service.webapp.interaction.Labels.JsonLabels
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests.{EditUserRequest, RegisterUserRequest}
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
      .mapToJson
      .handleError
      .mapImplicitly
  }

  override def getUserByUsername(username: String): Observable[User] =
    makeRequest(client =>
      Observable.from(client.get(getUserURI(username)).sendFuture()))
      .mapToJson
      .mapImplicitly

  override def deleteUser(username: String): Observable[String] =
    makeRequest(client =>
      Observable.from(client.get(deleteUserURI(username)).sendFuture()))
      .mapToJson
      .handleError
      .map(_.getString(JsonLabels.usernameLabel))

  override def editUser(request: EditUserRequest): Observable[User] = {
    makeRequest(client =>
      Observable.from(client.put(editUserURI(request.username))
        .sendJsonObjectFuture(request)))
      .mapToJson
      .handleError
      .mapImplicitly
  }

  override def updateAccess(username: String): Observable[Unit] = {
    makeRequest(client =>
      Observable.from(client.put(userAccessURI(username)).sendFuture()))
      .toCompletable
  }

  private implicit class CustomBufferObservable(observable: Observable[HttpResponse[Buffer]]) {

    def mapToJson: Observable[JsonObject] =
      observable.map(bodyAsJsonObject(throw InternalException(emptyBodyErrorMessage)))

  }

  private implicit class CustomJsonObservable(observable: Observable[JsonObject]) {

    def handleError: Observable[JsonObject] =
      observable.onErrorResumeNext(cause => {
        cause.printStackTrace()
        Observable.error(UserServiceErrorException(cause))
      })

  }

}

private[impl] object UserRestApi {

  private val emptyBodyErrorMessage = "User service returned an empty body"

  private val createUserURI = "/users"

  private def deleteUserURI(username: String) = userURI(username)

  private def getUserURI(username: String) = userURI(username)

  private def editUserURI(username: String) = userURI(username)

  private def userURI(username: String) = s"/users/$username"

  private def userAccessURI(username: String) = userURI(username) + "/access"

  private def toUserServiceRegistrationRequest(registerUserRequest: RegisterUserRequest): JsonObject = {
    val regRequest: JsonObject = registerUserRequest
    regRequest.remove(JsonLabels.passwordLabel)
    regRequest.remove(JsonLabels.passwordConfirmLabel)
    regRequest
  }

}
