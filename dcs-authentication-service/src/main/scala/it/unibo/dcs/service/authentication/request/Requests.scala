package it.unibo.dcs.service.authentication.request

/** It wraps all requests used by request handler, use cases, it.unibo.dcs.service.webapp.repositories, datastores and APIs */
object Requests {

  /** Request to check that the specified jwt token is valid */
  final case class CheckTokenRequest(token: String)

  /** Request to login the user with the specified user credentials */
  final case class LoginUserRequest(username: String, password: String) extends TokenRequest

  /** Request to logout the user, given the jwt token */
  final case class LogoutUserRequest(token: String)

  /** Request to delete the user, given the username */
  final case class DeleteUserRequest(username: String, token: String)

  /** Request to register the user with the specified user credentials */
  final case class RegisterUserRequest(username: String, password: String) extends TokenRequest

  /** Request that contains user credentials */
  trait TokenRequest {
    def username: String
    def password: String
  }

}
