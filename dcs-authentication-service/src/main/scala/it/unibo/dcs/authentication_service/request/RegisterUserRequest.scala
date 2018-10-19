package it.unibo.dcs.authentication_service.request

/** Request to register the user with the specified user credentials*/
final case class RegisterUserRequest(username: String, password: String) extends TokenRequest
