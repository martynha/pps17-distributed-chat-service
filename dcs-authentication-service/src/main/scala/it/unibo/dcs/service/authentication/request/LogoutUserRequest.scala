package it.unibo.dcs.service.authentication.request

/** Request to logout the user, given the jwt token */
final case class LogoutUserRequest(token: String)
