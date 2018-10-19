package it.unibo.dcs.authentication_service.request

/** Request to logout the user, given the jwt token*/
final case class LogoutUserRequest(token: String)
