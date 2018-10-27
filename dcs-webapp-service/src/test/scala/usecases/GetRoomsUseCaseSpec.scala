package usecases

import java.util.Date

import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, GetRoomsRequest}
import it.unibo.dcs.service.webapp.interaction.Results.GetRoomsResult
import it.unibo.dcs.service.webapp.model.{Room, User}
import it.unibo.dcs.service.webapp.repositories.RoomRepository
import it.unibo.dcs.service.webapp.usecases.GetRoomsUseCase
import it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers.GetRoomsSubscriber
import rx.lang.scala.{Observable, Subscriber}

class GetRoomsUseCaseSpec extends UseCaseSpec {
  private val user = User("martynha", "Martina", "Magnani", "bio", visible = true, new Date)
  private val room = Room("AulaMagna")
  private val rooms = List(room, room, room)

  private val getRoomsRequest = GetRoomsRequest(user.username, token)

  private val checkTokenRequest = CheckTokenRequest(token)

  private val getRoomsResult = GetRoomsResult(rooms)

  private val roomRepository: RoomRepository = mock[RoomRepository]

  private val getRoomsSubscriber: Subscriber[GetRoomsResult] = stub[Subscriber[GetRoomsResult]]

  private val getRoomsUseCase = new GetRoomsUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)

  it should "returns all rooms when the use case is executed" in {
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
