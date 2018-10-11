package it.unibo.dcs.service.room

import io.vertx.lang.scala.json.JsonObject

package object request {

  final case class CreateUserRequest(username: String) extends AnyVal

  final case class CreateRoomRequest(name: String, username: String)

  final case class DeleteRoomRequest(name: String, username: String)

  object Implicits {
    implicit def requestToJsonObject(request: DeleteRoomRequest): JsonObject = new JsonObject()
      .put("name", request.name)
      .put("owner_name", request.username)
  }

}
