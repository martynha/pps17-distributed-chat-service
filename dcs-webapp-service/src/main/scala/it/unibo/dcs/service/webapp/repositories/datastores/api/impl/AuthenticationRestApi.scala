package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.lang.scala.json.{Json, JsonObject}
import it.unibo.dcs.commons.RxHelper.Implicits.RichObservable
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.exceptions.{AuthServiceErrorException, InternalException, bodyAsJsonObject}
import it.unibo.dcs.service.webapp.interaction.Labels.JsonLabels
import it.unibo.dcs.service.webapp.interaction.Requests.Implicits.requestToJsonObject
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
    .map(getToken)

  override def registerUser(request: RegisterUserRequest): Observable[String] = {
    makeRequest(client =>
      Observable.from(client.post(registerUserURI).sendJsonObjectFuture(toRegisterUserRequest(request))))
      .map(bodyAsJsonObject(throw InternalException("Authentication service returned an empty body")))
      .map(getToken)
  }

  override def logoutUser(request: LogoutUserRequest): Observable[Unit] = {
    makeRequest(client =>
      Observable.from(client.delete(logoutUserURI)
        .putHeader(authenticationKeyLabel, bearer(request.token))
        .sendJsonObjectFuture(toLogoutUserRequest(request))))
      .map(bodyAsJsonObject(Json.emptyObj()))
      .toCompletable
  }

  override def checkToken(request: CheckTokenRequest): Observable[Unit] =
    makeRequest(client =>
      Observable.from(client.get(checkTokenURI(request.username))
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

  private def checkTokenURI(username: String) = "/protected/verify/" + username

  /* JWT Token labels */
  private val authenticationKeyLabel = "Authorization"

  private def bearer(token: String) = s"Bearer $token"

  private[impl] def getToken(json: JsonObject): String = {
    json.getString("token")
  }

  private[impl] def toRegisterUserRequest(registerUserRequest: RegisterUserRequest): JsonObject = {
    Json.obj((JsonLabels.usernameLabel, registerUserRequest.username),
      (JsonLabels.passwordLabel, registerUserRequest.password))
  }

  private def toLogoutUserRequest(request: LogoutUserRequest) = {
    Json.obj((JsonLabels.usernameLabel, request.username))
  }

  def deleteUserURI(username: String): String = s"/protected/users/$username"

}
