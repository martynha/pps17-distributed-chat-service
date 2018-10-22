package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.Json
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions._
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits._
import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.repositories.datastores.api.AuthenticationApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.AuthenticationRestApi._
import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global

class AuthenticationRestApi(private[this] val discovery: HttpEndpointDiscovery)
  extends AbstractApi(discovery, "authentication-service") with AuthenticationApi {

  /* JWT Token labels */
  private val authenticationKeyLabel = "Authorization"
  private val tokenPrefix = "Bearer "

  override def loginUser(loginUserRequest: LoginUserRequest): Observable[String] = {
    request(authWebClient =>
      Observable.from(authWebClient.post(loginUserURI).sendJsonObjectFuture(loginUserRequest)))
      .map(response => responseStatus(response) match {
        case HttpResponseStatus.OK =>
          response.bodyAsJsonObject()
            .getOrElse(throw LoginResponseException("Authentication service returned an empty body"))
            .getString("token")
        case _ =>
          val errorJson = response.bodyAsJsonObject().getOrElse(
            throw LoginResponseException("Authentication service returned an empty body after an error"))
          throw AuthServiceErrorException(errorJson)
      })
  }

  override def registerUser(registerRequest: RegisterUserRequest): Observable[String] = {
    request(authWebClient =>
      Observable.from(authWebClient.post(registerUserURI).sendJsonObjectFuture(registerRequest)))
      .map(response => responseStatus(response) match {
        case HttpResponseStatus.OK =>
          response.bodyAsJsonObject()
            .getOrElse(throw AuthRegistrationResponseException("Authentication service returned an empty body"))
            .getString("token")
        case _ =>
          val errorJson = response.bodyAsJsonObject()
            .getOrElse(throw AuthRegistrationResponseException(
              "Authentication service returned an empty body after an error"))
          throw AuthServiceErrorException(errorJson)
      })
  }

  override def logoutUser(logoutRequest: LogoutUserRequest): Observable[Unit] = {
    request(authWebClient =>
      Observable.from(authWebClient.post(logoutUserURI)
        .putHeader(authenticationKeyLabel, tokenPrefix + logoutRequest.token)
        .sendJsonObjectFuture(logoutRequest)))
      .map(response => responseStatus(response) match {
        case HttpResponseStatus.OK => ()
        case _ =>
          val errorJson = response.bodyAsJsonObject()
            .getOrElse(throw LogoutResponseException("Authentication service returned an empty body after an error"))
          throw AuthServiceErrorException(errorJson)
      })
  }

  override def checkToken(checkTokenRequest: CheckTokenRequest): Observable[Unit] =
    request(tokenWebClient =>
      Observable.from(tokenWebClient.get(checkTokenURI)
        .putHeader(authenticationKeyLabel, tokenPrefix + checkTokenRequest.token)
        .sendJsonObjectFuture(Json.obj())))
      .map(response => responseStatus(response) match {
        case HttpResponseStatus.OK => ()
        case _ =>
          val errorJson = response.bodyAsJsonObject()
            .getOrElse(throw TokenCheckResponseException("Authentication service returned an empty body after an error"))
          throw AuthServiceErrorException(errorJson)
      })

  override def deleteUser(deleteUserRequest: DeleteUserRequest): Observable[Unit] = {
    request(authWebClient =>
      Observable.from(authWebClient.delete(deleteUserURI(deleteUserRequest.username))
        .putHeader(authenticationKeyLabel, tokenPrefix + deleteUserRequest.token)
        .sendFuture()))
      .map(response => responseStatus(response) match {
        case HttpResponseStatus.OK => ()
        case _ =>
          val errorJson = response.bodyAsJsonObject()
            .getOrElse(
              throw DeleteUserResponseException("Authentication service returned an empty body after an error"))
          throw AuthServiceErrorException(errorJson)
      })
  }

}

private[impl] object AuthenticationRestApi {

  val loginUserURI = "/login"
  val registerUserURI = "/register"
  val logoutUserURI = "/protected/logout"
  val checkTokenURI = "/protected/tokenValidity"
  val checkLogoutURI = "/validateLogout"

  def deleteUserURI(username: String) = s"/user/$username"
}
