package it.unibo.dcs.authentication_service.request

/** Request that contains user credentials */
trait TokenRequest {

  def username: String
  def password: String

}
