package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.scala.ext.web.client.WebClient
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{DeleteUserResponseException, GetUserResponseException, UserServiceErrorException}
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.exceptions.UserCreationResponseException
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.UserRestApi._
import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global

class UserRestApi(private[this] val discovery: HttpEndpointDiscovery)
  extends AbstractApi(discovery, "user-service") with UserApi {

  override def createUser(registrationRequest: RegisterUserRequest): Observable[User] = {
    request((userWebClient: WebClient) =>
      Observable.from(userWebClient.post(createUserURI).sendJsonObjectFuture(registrationRequest)))
      .map(response => response.bodyAsJsonObject().getOrElse(throw UserCreationResponseException()))
  }

  override def getUserByUsername(username: String): Observable[User] =
    request((userWebClient: WebClient) =>
      Observable.from(userWebClient.get(getUserURI(username)).sendFuture()))
      .map(response => response.bodyAsJsonObject()
        .getOrElse(throw GetUserResponseException("User service returned an empty body")))

  override def deleteUser(username: String): Observable[Unit] =
    request(userWebClient =>
      Observable.from(userWebClient.post(deleteUserURI(username)).sendFuture()))
      .map(response => responseStatus(response) match {
        case HttpResponseStatus.OK => ()
        case _ =>
          val errorJson = response.bodyAsJsonObject()
            .getOrElse(throw DeleteUserResponseException("User service returned an empty body after an error"))
          throw UserServiceErrorException(errorJson, username)
      })

}

private[impl] object UserRestApi {

  val createUserURI = "/createUser"
  val validateRegistration = "/validateRegistration"

  def deleteUserURI(username: String) = s"/deleteUser/$username"

  def getUserURI(username: String) = s"/getUser/$username"
}
