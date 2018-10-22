package it.unibo.dcs.service.authentication.request

/** Request to register the user with the specified user credentials */
final case class RegisterUserRequest(username: String, password: String) extends TokenRequest
