package it.unibo.dcs.service.user

package object request {

  final case class CreateUserRequest(username: String, firstName: String, lastName: String)

  final case class GetUserRequest(username: String) extends AnyVal

}
