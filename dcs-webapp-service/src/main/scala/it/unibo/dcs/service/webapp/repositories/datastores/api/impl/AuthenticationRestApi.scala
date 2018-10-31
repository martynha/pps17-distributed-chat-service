package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.lang.scala.json.{Json, JsonObject}
import it.unibo.dcs.commons.RxHelper.Implicits.RichObservable
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

  override def loginUser(request: LoginUserRequest): Observable[String] = makeRequest(client =>
    Observable.from(client.post(loginUserURI).sendJsonObjectFuture(request)))
    .map(bodyAsJsonObject(throw InternalException("Authentication service returned an empty body")))
    .mapImplicitly

  override def registerUser(request: RegisterUserRequest): Observable[String] = {
    makeRequest(client =>
      Observable.from(client.post(registerUserURI).sendJsonObjectFuture(request)))
      .map(bodyAsJsonObject(throw InternalException("Authentication service returned an empty body")))
      .mapImplicitly
  }

  override def logoutUser(request: LogoutUserRequest): Observable[Unit] = {
    makeRequest(client =>
      Observable.from(client.post(logoutUserURI)
        .putHeader(authenticationKeyLabel, bearer(request.token))
        .sendJsonObjectFuture(request)))
      .map(bodyAsJsonObject(Json.emptyObj()))
      .toCompletable
  }

  override def checkToken(request: CheckTokenRequest): Observable[Unit] =
    makeRequest(client =>
      Observable.from(client.get(checkTokenURI)
        .putHeader(authenticationKeyLabel, bearer(request.token))
        .sendJsonObjectFuture(Json.obj())))
      .map(bodyAsJsonObject())
      .toCompletable

  override def deleteUser(request: DeleteUserRequest): Observable[Unit] = {
    makeRequest(client =>
      Observable.from(client.post(deleteUserURI(request.username))
        .putHeader(authenticationKeyLabel, bearer(request.token))
        .sendJsonObjectFuture(request)))
      .map(bodyAsJsonObject())
      .onErrorResumeNext(cause => Observable.error(AuthServiceErrorException(cause)))
      .toCompletable
  }

}

private[impl] object AuthenticationRestApi {

  private val loginUserURI = "/login"
  private val registerUserURI = "/register"
  private val logoutUserURI = "/protected/logout"
  private val checkTokenURI = "/protected/tokenValidity"
  private val checkLogoutURI = "/validateLogout"

  /* JWT Token labels */
  private val authenticationKeyLabel = "Authorization"
  private def bearer(token: String) = s"Bearer $token"

  private[impl] def getToken(json: JsonObject): String = {
    json.getString("token")
  }

  def deleteUserURI(username: String) = s"/user/$username"

}
