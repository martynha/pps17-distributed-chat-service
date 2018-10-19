package it.unibo.dcs.service.webapp.repositories.datastores.api

/** It contains all the exceptions used by the APIs */
package object exceptions {

  final case class RegistrationResponseException() extends RuntimeException

  final case class LoginResponseException() extends RuntimeException

  final case class GetUserResponseException() extends RuntimeException

  final case class UserCreationResponseException() extends RuntimeException

  final case class RoomDeletionResponseException() extends RuntimeException

  final case class RoomCreationResponseException() extends RuntimeException

  final case class RegistrationValidityResponseException() extends RuntimeException

}
