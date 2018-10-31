package it.unibo.dcs.service.webapp.usecases

import java.util.Date

import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, RoomJoinRequest}
import it.unibo.dcs.service.webapp.interaction.Results.RoomJoinResult
import it.unibo.dcs.service.webapp.model.{Participation, Room}
import it.unibo.dcs.service.webapp.repositories.RoomRepository
import it.unibo.dcs.service.webapp.usecases.JoinRoomUseCase
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class JoinRoomUseCaseSpec extends UseCaseSpec {

  private val roomRepository: RoomRepository = mock[RoomRepository]

  private val room = Room("Room 1")
  private val participation = Participation(new Date(), room, user)

  private val joinRoomRequest = RoomJoinRequest(room.name, user.username, token)

  private val joinRoomSubscriber = stub[Subscriber[RoomJoinResult]]
  private val joinRoomResult = RoomJoinResult(participation)

  private val joinRoomUseCase =
    new JoinRoomUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)

  it should "execute the room join use case" in {
    // Given
    (authRepository checkToken _) expects CheckTokenRequest(token) returns (Observable empty) once()
    (roomRepository joinRoom _) expects joinRoomRequest returns (Observable just participation) once()

    // When
    // createUserUseCase is executed with argument `request`
    joinRoomUseCase(joinRoomRequest) subscribe joinRoomSubscriber

    // Then
    (joinRoomSubscriber onNext _) verify joinRoomResult once()
    (() => joinRoomSubscriber onCompleted) verify() once()
  }
}
