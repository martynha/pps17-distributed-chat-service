package it.unibo.dcs.service.webapp.model

import java.util.Date

/** Model class that represents a user participation in a room
  *
  * @param joinDate date of the room join
  * @param room     the room joined by the user
  * @param username     the username of the user who joined the room
  */
case class Participation(joinDate: Date, room: Room, username: String)