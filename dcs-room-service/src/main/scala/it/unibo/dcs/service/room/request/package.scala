package it.unibo.dcs.service.room

import java.util.Date

package object request {

  final case class CreateUserRequest(username: String)

  final case class GetRoomRequest(name: String)

  final case class CreateRoomRequest(name: String, username: String)

  final case class DeleteRoomRequest(name: String, username: String)

  final case class GetRoomsRequest(username: String)

  final case class JoinRoomRequest(name: String, username: String)

  final case class SendMessageRequest(name: String, username: String, content: String, timestamp: Date)
  
  final case class GetUserParticipationsRequest(username: String)

}
