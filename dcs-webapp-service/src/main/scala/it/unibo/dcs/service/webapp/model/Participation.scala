package it.unibo.dcs.service.webapp.model

import java.util.Date

import io.vertx.lang.scala.json.{Json, JsonObject}
import it.unibo.dcs.service.webapp.gson
import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson

import scala.language.implicitConversions

/** Model class that represents a user participation in a room
  *
  * @param joinDate date of the room join
  * @param room     the room joined by the user
  * @param username the user who joined the room
  */
case class Participation(room: Room, username: String, joinDate: Date)

object Participation {

  object Implicits {

    implicit def participationToJsonObject(participation: Participation): JsonObject =
      gson toJsonObject participation
  }

}
