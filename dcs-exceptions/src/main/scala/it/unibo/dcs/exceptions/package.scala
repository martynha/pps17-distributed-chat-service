package it.unibo.dcs

import io.vertx.core.json.JsonObject

/** It contains all the exceptions used by the APIs */
package object exceptions {

  /** Sum type representing all the specific exceptions for Distributed Chat Service application */
  sealed trait DcsException extends RuntimeException

  /* Already taken */
  final case class UsernameAlreadyTaken(username: String) extends DcsException

  /* Not found*/
  final case class UserNotFoundException(username: String) extends DcsException

  /* Missing information */
  final case class MissingUsernameException(message: String) extends DcsException

  final case class MissingLastNameException(message: String) extends DcsException

  final case class MissingPasswordException(message: String) extends DcsException

  final case class MissingFirstNameException(message: String) extends DcsException

  final case class MissingTokenException(message: String) extends DcsException

  final case class MissingRoomNameException(message: String) extends DcsException


  /* Response exceptions */
  final case class RegistrationResponseException(message: String, username: String, token: String) extends DcsException

  final case class LoginResponseException(message: String) extends DcsException

  final case class LogoutResponseException(message: String) extends DcsException

  final case class GetUserResponseException(message: String) extends DcsException

  final case class UserCreationResponseException(message: String, username: String, token: String) extends DcsException

  final case class RoomDeletionResponseException(message: String) extends DcsException

  final case class RoomCreationResponseException(message: String) extends DcsException

  final case class TokenCheckResponseException(message: String) extends DcsException

  final case class AuthRegistrationResponseException(message: String) extends DcsException

  final case class DeleteUserResponseException(message: String) extends DcsException

  final case class UserServiceErrorException(errorJson: JsonObject,
                                             username: String,
                                             token: String = "") extends DcsException

  final case class AuthServiceErrorException(errorJson: JsonObject) extends DcsException

  final case class RoomServiceErrorException(errorJson: JsonObject,
                                             username: String,
                                             token: String) extends DcsException

}

