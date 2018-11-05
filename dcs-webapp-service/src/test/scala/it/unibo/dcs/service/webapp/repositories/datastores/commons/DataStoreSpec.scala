package it.unibo.dcs.service.webapp.repositories.datastores.commons

import java.util.Date

import it.unibo.dcs.service.webapp.interaction.Requests.{EditUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.model.{Participation, Room, User}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}

/**
  * Abstract class that encapsulates the objects used by more than one data store test.
  */
abstract class DataStoreSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  /** ----------------- Model instances ---------------------------------------------------- */
  /** Users */
  protected val user = User("niklegend", "nicola", "piscaglia", "bla", visible = true, new Date())

  /** Tokens */
  protected val token = "token"

  /** Rooms */
  protected val room = Room("Room 1")
  protected val rooms: List[Room] = List(room, room, room)

  /** Participations */
  protected val participation = Participation(new Date(), room, user.username)
  protected val participations = Set(participation)

  /** ----------------- Requests ------------------------------------------------------------ */
  protected val registerRequest = RegisterUserRequest(user.username, user.firstName, user.lastName,
    "password", "password")

  protected val editUserRequest =
    EditUserRequest(user.username, user.firstName, user.lastName, user.bio, user.visible, token)
}
