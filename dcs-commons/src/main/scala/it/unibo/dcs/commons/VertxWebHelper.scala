package it.unibo.dcs.commons

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.ext.web.RoutingContext
import io.vertx.scala.ext.web.client.HttpResponse
import it.unibo.dcs.commons.VertxWebHelper.Implicits._

import scala.language.implicitConversions

object VertxWebHelper {

  def getContextData(urlParameter: String)(implicit context: RoutingContext): Option[String] =
    context.request.getParam(urlParameter)

  def getJsonBodyData(fieldName: String)(implicit context: RoutingContext): Option[String] =
    context.getBody().map(body => body.toJsonObject.getValue(fieldName)).map(value => value.toString)

  def respondWithCode(statusCode: Int)(implicit context: RoutingContext): Unit =
    context.response.setStatusCode(statusCode).end

  def respond(statusCode: Int, message: String)(implicit context: RoutingContext): Unit = {
    val responseBody = Json.obj(("status", statusCode), ("message", message))
    context.response.setStatusCode(statusCode).end(responseBody.encodePrettily())
  }

  def isCodeSuccessful(statusCode: Int): Boolean = statusCode / 100 == 2

  def doIfDefined(id: Option[_], action: => Unit)(implicit context: RoutingContext): Unit =
    if (id.isDefined) {
      action
    } else respondWithCode(400)

  def getTokenFromHeader(implicit context: RoutingContext): Option[String] = {
    context.request().headers().get(HttpHeaders.AUTHORIZATION.toString).map(token => token.split(" ").last)
  }

  def endErrorResponse(response: HttpServerResponse,
                       httpResponseStatus: HttpResponseStatus,
                       errorType: String, description: String): Unit = {
    val statusJson: JsonObject = httpResponseStatus
    val typeField = ("type", errorType)
    val descriptionField = ("description", description)
    response.setStatusCode(httpResponseStatus.code())
    response.end(Json.obj(("status", statusJson), typeField, descriptionField))
  }

  def responseStatus(response: HttpResponse[Buffer]): HttpResponseStatus = {
    HttpResponseStatus.valueOf(response.statusCode())
  }

  object Implicits {

    implicit def jsonObjectToString(json: JsonObject): String = json.encode()

    implicit def httpResponseStatusToJsonObject(status: HttpResponseStatus): JsonObject =
      new JsonObject().put("code", status.code()).put("reasonPhrase", status.reasonPhrase())
  }

}
