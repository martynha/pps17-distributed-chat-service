package it.unibo.dcs.service.webapp.interaction

import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import it.unibo.dcs.service.webapp.gson
import it.unibo.dcs.service.webapp.interaction.Labels.JsonLabels.tokenLabel
import it.unibo.dcs.service.webapp.model.{Participation, Room, User}

import scala.language.implicitConversions

/** It wraps all results produced by the use cases executions. */
object Results {

  /** Sum type that represents all the use cases results used in Distributed Chat Service application */
  sealed trait DcsResult

  final case class LoginResult(user: User, token: String) extends DcsResult

  final case class RegisterResult(user: User, token: String) extends DcsResult

  final case class RoomCreationResult(room: Room) extends DcsResult

  final case class RoomJoinResult(participation: Participation) extends DcsResult

  final case class GetRoomsResult(rooms: List[Room]) extends DcsResult

  /** It enables implicit conversions in order to clean code that deals with results. */
  object Implicits {

    implicit def registrationResultToJsonString(result: RegisterResult): String =
      resultToJsonString(result.user, _.put(tokenLabel, result.token))

    implicit def loginResultToJsonString(result: LoginResult): String =
      resultToJsonString(result.user, _.put(tokenLabel, result.token))

    implicit def roomCreationResultToJsonString(result: RoomCreationResult): String = resultToJsonString(result.room)

    implicit def roomJoinResultToJsonString(result: RoomJoinResult): String = resultToJsonString(result.participation)

    private def resultToJsonString(result: Product, transformations: JsonObject => JsonObject = identity): String =
      transformations(Json.fromObjectString(gson.toJson(result))) encode()

    implicit def registrationResultToJsonObject(result: RegisterResult): JsonObject = {
      Json.fromObjectString(gson.toJson(result.user)).put(tokenLabel, result.token)
    }

    implicit def loginResultToJsonObject(result: LoginResult): JsonObject = {
      Json.fromObjectString(gson.toJson(result.user)).put(tokenLabel, result.token)
    }

    implicit def roomCreationResultToJsonObject(result: RoomCreationResult): JsonObject = {
      Json.fromObjectString(gson.toJson(result.room))
    }

    implicit def roomJoinResultToJsonObject(result: RoomJoinResult): JsonObject = {
      Json.fromObjectString(gson.toJson(result.participation))
    }

    implicit def getRoomsToJsonArray(result: GetRoomsResult): JsonArray = {
      result.rooms
        .map(x => Json.fromObjectString(gson.toJson(x)))
        .foldLeft(new JsonArray)(_ add _)
    }

  }

}
