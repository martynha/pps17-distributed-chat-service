package it.unibo.dcs.service.user

import io.vertx.lang.scala.json.JsonArray

package object request {

  final case class CreateUserRequest(username: String, firstName: String, lastName: String)

  final case class GetUserRequest(username: String) extends AnyVal

  object Implicits {

    implicit def requestToParams(request: CreateUserRequest): JsonArray =
      new JsonArray().add(request.username).add(request.firstName).add(request.lastName)

    implicit def requestToParams(request: GetUserRequest): JsonArray =
      new JsonArray().add(request.username)

  }

}
