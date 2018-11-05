package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.GetRoomParticipationsRequest
import it.unibo.dcs.service.webapp.interaction.Results.GetRoomParticipationsResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class GetRoomParticipationsUseCaseSpec extends UseCaseSpec {

  private val getRoomParticipationsRequest = GetRoomParticipationsRequest(room.name, user.username, token)

  private val getRoomsParticipationsResult = GetRoomParticipationsResult(participations)

  private val getRoomParticipationsSubscriber: Subscriber[GetRoomParticipationsResult] =
    stub[Subscriber[GetRoomParticipationsResult]]

  private val getRoomParticipationsUseCase = new GetRoomParticipationsUseCase(threadExecutor, postExecutionThread,
    authRepository, roomRepository)

  it should "return all the participations for given room when the use case is executed" in {
    // Given
    (authRepository checkToken _) expects checkTokenRequest returns (Observable just token)
    (roomRepository getRoomParticipations _) expects getRoomParticipationsRequest returns
      (Observable just participations)

    // When
    // getRoomsUseCase is executed with argument `request`
    getRoomParticipationsUseCase(getRoomParticipationsRequest) subscribe getRoomParticipationsSubscriber

    // Then
    (getRoomParticipationsSubscriber onNext _) verify getRoomsParticipationsResult once()
    (() => getRoomParticipationsSubscriber onCompleted) verify() once()
  }

}
