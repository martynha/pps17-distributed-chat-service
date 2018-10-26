package repositories

import java.util.Date

import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.model.User
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}

abstract class RepositorySpec extends FlatSpec with MockFactory with OneInstancePerTest {

  protected val user = User("niklegend", "nicola", "piscaglia", "bla", visible = true, new Date())

  protected val registerRequest = RegisterUserRequest(user.username, user.firstName,
    user.lastName, "password", "password")
}
