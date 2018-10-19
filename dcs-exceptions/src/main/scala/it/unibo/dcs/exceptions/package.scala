package it.unibo.dcs

package object exceptions {

  final case class UsernameAlreadyTaken(username: String) extends RuntimeException

  final case class MissingUsernameException(message: String) extends RuntimeException

  final case class MissingLastNameException(message: String) extends RuntimeException

  final case class MissingFirstNameException(message: String) extends RuntimeException

}
