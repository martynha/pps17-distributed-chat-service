package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, CreateRoomRequest}
import it.unibo.dcs.service.webapp.interaction.Results.RoomCreationResult
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.RoomRepository
import it.unibo.dcs.service.webapp.usecases.CreateRoomUseCase
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class CreateRoomUseCaseSpec extends UseCaseSpec {

  private val room = Room("Room 1")

  private val createRoomRequest = CreateRoomRequest("Room 1", user.username, token)
  private val checkTokenRequest = CheckTokenRequest(token)

  private val roomCreationResult = RoomCreationResult(room)

  private val roomRepository: RoomRepository = mock[RoomRepository]

  private val roomCreationSubscriber: Subscriber[RoomCreationResult] = stub[Subscriber[RoomCreationResult]]

  private val createRoomUseCase = new CreateRoomUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)


  it should "create a new room when the use case is executed" in {
    // Given
    (roomRepository createRoom _) expects createRoomRequest returns (Observable just room)
    // userRepository is called with `registerRequest` as parameter returns an observable that contains only `user`
    (authRepository checkToken _) expects checkTokenRequest returns (Observable just token)

    // When
    // createUserUseCase is executed with argument `request`
    createRoomUseCase(createRoomRequest) subscribe roomCreationSubscriber

    // Then
    (roomCreationSubscriber onNext _) verify roomCreationResult once()
    (() => roomCreationSubscriber onCompleted) verify() once()
  }
}
