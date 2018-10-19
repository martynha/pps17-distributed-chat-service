package it.unibo.dcs.commons.service

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.core.http.HttpServerResponse

import scala.language.implicitConversions

trait ErrorHandler {

  protected def endErrorResponse(response: HttpServerResponse,
                                 httpResponseStatus: HttpResponseStatus,
                                 errorType: String, description: String): Unit = {
    val statusJson: JsonObject = httpResponseStatus
    val typeField = ("type", errorType)
    val descriptionField = ("description", description)
    response.setStatusCode(httpResponseStatus.code())
    response.end(Json.obj(("status", statusJson), typeField, descriptionField))
  }

  implicit def jsonObjectToString(json: JsonObject): String = json.encode()

  implicit def httpResponseStatusToJsonObject(status: HttpResponseStatus): JsonObject =
    new JsonObject().put("code", status.code()).put("reasonPhrase", status.reasonPhrase())
}

