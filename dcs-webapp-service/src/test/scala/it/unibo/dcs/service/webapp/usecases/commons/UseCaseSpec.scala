package it.unibo.dcs.service.webapp.usecases.commons

import java.util.Date

import it.unibo.dcs.service.webapp.interaction.Requests.CheckTokenRequest
import it.unibo.dcs.service.webapp.model.{Participation, Room, User}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}

/**
  * Abstract class that encapsulates the objects used by more than one Use Case test.
  */
abstract class UseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  /** ----------------- Model instances ---------------------------------------------------- */
  /** Users */
  protected val user = User("niklegend", "Nicola", "Piscaglia", "bio", visible = true, new Date)
  private val secondUser = User("mvandi", "Mattia", "Vandi", "bio", visible = true, new Date)

  /** Tokens */
  protected val token = "token"

  /** Rooms */
  protected val roomName = "Room 1"
  protected val room = Room(roomName)
  protected val rooms = List(Room("Room1"), Room("Room2"), Room("Room3"))

  /** Participations */
  protected val participation = Participation(new Date(), room, user.username)
  protected val secondParticipation = Participation(new Date(), room, secondUser.username)
  protected val participations = Set(participation, secondParticipation)

  /** ----------------- Requests ------------------------------------------------------------ */
  protected val checkTokenRequest = CheckTokenRequest(token, user.username)

}
