package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.GetRoomsRequest
import it.unibo.dcs.service.webapp.interaction.Results.GetRoomsResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class GetRoomsUseCaseSpec extends UseCaseSpec {

  private val getRoomsRequest = GetRoomsRequest(user.username, token)

  private val getRoomsResult = GetRoomsResult(rooms)

  private val getRoomsSubscriber: Subscriber[GetRoomsResult] = stub[Subscriber[GetRoomsResult]]

  private val getRoomsUseCase = new GetRoomsUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)

  it should "return all rooms when the use case is executed" in {
    // Given
    (authRepository checkToken _) expects checkTokenRequest returns (Observable just token)

    (roomRepository getRooms _) expects getRoomsRequest returns (Observable just rooms)

    // When
    // getRoomsUseCase is executed with argument `request`
    getRoomsUseCase(getRoomsRequest) subscribe getRoomsSubscriber

    // Then
    (getRoomsSubscriber onNext _) verify getRoomsResult once()
    (() => getRoomsSubscriber onCompleted) verify() once()
  }

}
