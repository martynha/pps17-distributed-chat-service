package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.GetUserParticipationsRequest
import it.unibo.dcs.service.webapp.interaction.Results.GetUserParticipationsResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class GetUserParticipationsUseCaseSpec extends UseCaseSpec {

  private val getUserParticipationsRequest = GetUserParticipationsRequest(user.username, token)

  private val result = GetUserParticipationsResult(rooms)

  private val useCase = new GetUserParticipationsUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)

  private val subscriber = stub[Subscriber[GetUserParticipationsResult]]

  it should "retrieve all the participations for the given user" in {
    // Given
    (authRepository checkToken _) expects checkTokenRequest returns (Observable just token)

    (roomRepository getUserParticipations _) expects getUserParticipationsRequest returns (Observable just rooms)

    // When
    // getRoomsUseCase is executed with argument `request`
    useCase(getUserParticipationsRequest) subscribe subscriber

    // Then
    (subscriber onNext _) verify result once()
    (() => subscriber onCompleted) verify() once()
  }

}
