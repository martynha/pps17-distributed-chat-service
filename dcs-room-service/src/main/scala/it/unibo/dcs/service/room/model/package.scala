package it.unibo.dcs.service.room

import java.util.Date

package object model {

  final case class Room(name: String)

  final case class Participation(name: Room, username: String, joinDate: Date)

  final case class User(username: String)
}
