package it.unibo.dcs.service.room

import java.util.Date

package object model {

  final case class Room(name: String)

  final case class Participation(room: Room, username: String, joinDate: Date)

  final case class User(username: String)

  final case class Message(room: Room, username: String, content: String, timestamp: Date)
}
