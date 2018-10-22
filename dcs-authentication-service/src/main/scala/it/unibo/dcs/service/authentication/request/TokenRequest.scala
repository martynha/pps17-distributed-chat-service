package it.unibo.dcs.service.authentication.request

/** Request that contains user credentials */
trait TokenRequest {

  def username: String

  def password: String

}
