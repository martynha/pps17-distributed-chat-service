package it.unibo.dcs.service.room.model

package object execption {
  final case class RoomNotFoundException(name: String) extends RuntimeException
}
