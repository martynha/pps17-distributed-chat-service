package it.unibo.dcs.service.authentication.request

/** Request to check that the specified jwt token is valid */
final case class CheckTokenRequest(token: String)