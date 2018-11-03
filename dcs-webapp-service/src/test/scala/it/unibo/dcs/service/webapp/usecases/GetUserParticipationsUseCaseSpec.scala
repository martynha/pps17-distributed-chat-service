package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, GetUserParticipationsRequest}
import it.unibo.dcs.service.webapp.interaction.Results.GetUserParticipationsResult
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.RoomRepository
import rx.lang.scala.{Observable, Subscriber}

class GetUserParticipationsUseCaseSpec extends UseCaseSpec {

  private val rooms = List(Room("Room1"), Room("Room2"), Room("Room3"))

  private val getUserParticipationsRequest = GetUserParticipationsRequest(user.username, token)

  private val checkTokenRequest = CheckTokenRequest(token, user.username)

  private val result = GetUserParticipationsResult(rooms)

  private val roomRepository = mock[RoomRepository]

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
