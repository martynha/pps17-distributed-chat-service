package it.unibo.dcs.service.user

import java.util.Date

import io.vertx.lang.scala.json.JsonObject
import it.unibo.dcs.commons.dataaccess.Implicits._

package object model {

  final case class User(username: String, firstName: String, lastName: String, bio: String, visible: Boolean, lastSeen: Date)

}
