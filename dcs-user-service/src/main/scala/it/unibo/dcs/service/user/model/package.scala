package it.unibo.dcs.service.user

import java.util.Date

import io.vertx.lang.scala.json.JsonObject
import it.unibo.dcs.commons.dataaccess.Implicits._

package object model {

  final case class User(username: String, firstName: String, lastName: String, bio: String, visible: Boolean, lastSeen: Date)

  object Implicits {

    implicit def jsonObjectToUser(userJsonObject: JsonObject): User = {
      User(userJsonObject.getString("username"),
        userJsonObject.getString("first_name"),
        userJsonObject.getString("last_name"),
        userJsonObject.getString("bio"),
        userJsonObject.getString("visible"),
        userJsonObject.getString("last_seen"))
    }

    implicit def userToJsonObject(user: User): JsonObject = {
      new JsonObject()
        .put("username", user.username)
        .put("first_name", user.firstName)
        .put("last_name", user.lastName)
        .put("bio", user.bio)
        .put("visible", booleanToString(user.visible))
        .put("last_seen", dateToString(user.lastSeen))
    }

  }

}
