package it.unibo.dcs.authentication_service.request

/** Request to check that the specified jwt token is valid*/
final case class CheckTokenRequest(token: String)