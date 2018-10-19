package it.unibo.dcs.authentication_service.request

/** Request to login the user with the specified user credentials*/
final case class LoginUserRequest(username: String, password: String) extends TokenRequest
