package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, DeleteRoomRequest}
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.RoomRepository
import it.unibo.dcs.service.webapp.usecases.DeleteRoomUseCase
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class DeleteRoomUseCaseSpec extends UseCaseSpec {

  private val roomName = "Room 1"
  private val room = Room(roomName)
  private val deleteRoomRequest = DeleteRoomRequest(room.name, user.username, token)

  private val roomRepository: RoomRepository = mock[RoomRepository]

  private val roomDeletionSubscriber = stub[Subscriber[String]]

  private val deleteRoomUseCase =
    new DeleteRoomUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)


  it should "delete the chosen room when the use case is executed" in {
    // Given
    (roomRepository deleteRoom _) expects deleteRoomRequest returns (Observable just roomName)
    // userRepository is called with `registerRequest` as parameter returns an observable that contains only `user`
    (authRepository checkToken _) expects CheckTokenRequest(token) returns (Observable just roomName)

    // When
    // createUserUseCase is executed with argument `request`
    deleteRoomUseCase(deleteRoomRequest) subscribe roomDeletionSubscriber

    // Then
    (roomDeletionSubscriber onNext _) verify roomName once()
  }
}
