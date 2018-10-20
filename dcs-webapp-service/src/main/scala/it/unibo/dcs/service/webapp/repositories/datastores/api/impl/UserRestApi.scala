package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.scala.ext.web.client.WebClient
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{GetUserResponseException, RegistrationValidityResponseException, UserCreationResponseException}
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
    request((userWebClient: WebClient) =>
      Observable.from(userWebClient.post(createUserURI).sendJsonObjectFuture(registrationRequest)))
      .map(response => response.bodyAsJsonObject()
        .getOrElse(throw UserCreationResponseException("User service returned an empty body")))
  }

  override def getUserByUsername(username: String): Observable[User] =
    request((userWebClient: WebClient) =>
      Observable.from(userWebClient.get(getUserURI(username)).sendFuture()))
      .map(response => response.bodyAsJsonObject()
        .getOrElse(throw GetUserResponseException("User service returned an empty body")))

  override def checkUserRegistration(checkRegistrationRequest: RegisterUserRequest): Observable[Unit] =
    request((userWebClient: WebClient) =>
      Observable.from(userWebClient.post(validateRegistration).sendJsonObjectFuture(checkRegistrationRequest)))
      .map(response => response.bodyAsJsonObject()
        .getOrElse(throw RegistrationValidityResponseException("User service returned an empty body")))
}

private[impl] object UserRestApi {

  val createUserURI = "/createUser"

  val validateRegistration = "/validateRegistration"

  def getUserURI(username: String) = s"/getUser/$username"

}
