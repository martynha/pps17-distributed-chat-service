package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, RoomLeaveRequest}
import it.unibo.dcs.service.webapp.interaction.Results.RoomLeaveResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class LeaveRoomUseCaseSpec extends UseCaseSpec {

  private val leaveRoomRequest = RoomLeaveRequest(room.name, user.username, token)

  private val leaveRoomSubscriber = stub[Subscriber[RoomLeaveResult]]
  private val leaveRoomResult = RoomLeaveResult(participation)

  private val leaveRoomUseCase =
    new LeaveRoomUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)

  it should "execute the room leave use case" in {
    // Given
    (authRepository checkToken _) expects CheckTokenRequest(token, user.username) returns (Observable just Unit) once()
    (roomRepository leaveRoom _) expects leaveRoomRequest returns (Observable just participation) once()

    // When
    // createUserUseCase is executed with argument `request`
    leaveRoomUseCase(leaveRoomRequest) subscribe leaveRoomSubscriber

    // Then
    (leaveRoomSubscriber onNext _) verify leaveRoomResult once()
    (() => leaveRoomSubscriber onCompleted) verify() once()
  }
}
