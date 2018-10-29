package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.lang.scala.json.{Json, JsonObject}
import it.unibo.dcs.commons.RxHelper.unit
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{AuthServiceErrorException, InternalException, bodyAsJsonObject}
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

  override def loginUser(request: LoginUserRequest): Observable[String] = makeRequest(client =>
    Observable.from(client.post(loginUserURI).sendJsonObjectFuture(request)))
    .map(bodyAsJsonObject(throw InternalException("Authentication service returned an empty body")))
    .map(getToken)

  override def registerUser(request: RegisterUserRequest): Observable[String] = {
    makeRequest(client =>
      Observable.from(client.post(registerUserURI).sendJsonObjectFuture(request)))
      .map(bodyAsJsonObject(throw InternalException("Authentication service returned an empty body")))
      .map(getToken)
  }

  override def logoutUser(request: LogoutUserRequest): Observable[Unit] = {
    makeRequest(client =>
      Observable.from(client.post(logoutUserURI)
        .putHeader(authenticationKeyLabel, tokenPrefix + request.token)
        .sendJsonObjectFuture(request)))
      .map(bodyAsJsonObject(Json.emptyObj()))
      .map(unit)
  }

  override def checkToken(request: CheckTokenRequest): Observable[Unit] =
    makeRequest(client =>
      Observable.from(client.get(checkTokenURI)
        .putHeader(authenticationKeyLabel, tokenPrefix + request.token)
        .sendJsonObjectFuture(Json.obj())))
      .map(bodyAsJsonObject())
      .map(unit)

  override def deleteUser(request: DeleteUserRequest): Observable[Unit] = {
    makeRequest(client =>
      Observable.from(client.post(deleteUserURI(request.username)).sendJsonObjectFuture(request)))
      .map(bodyAsJsonObject())
      .map(unit)
      .onErrorResumeNext(cause => Observable.error(AuthServiceErrorException(cause)))
  }

}

private[impl] object AuthenticationRestApi {

  val loginUserURI = "/login"
  val registerUserURI = "/register"
  val logoutUserURI = "/protected/logout"
  val checkTokenURI = "/protected/tokenValidity"
  val checkLogoutURI = "/validateLogout"

  private[impl] def getToken(json: JsonObject): String = {
    json.getString("token")
  }

  def deleteUserURI(username: String) = s"/user/$username"

}
