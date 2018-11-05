package it.unibo.dcs.service.user

package object request {

  final case class CreateUserRequest(username: String, firstName: String, lastName: String)
    extends CreateOrEditUserRequest

  final case class GetUserRequest(username: String) extends AnyVal

  final case class EditUserRequest(username: String, firstName: String, lastName: String,
                                   bio: String, visible: Boolean) extends CreateOrEditUserRequest

  trait CreateOrEditUserRequest{
    val username: String
    val firstName: String
    val lastName: String
  }
}
