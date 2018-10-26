package repositories

import it.unibo.dcs.service.webapp.interaction.Requests.{CreateRoomRequest, DeleteRoomRequest}
import it.unibo.dcs.service.webapp.model.Room
import it.unibo.dcs.service.webapp.repositories.RoomRepository
import it.unibo.dcs.service.webapp.repositories.datastores.RoomDataStore
import it.unibo.dcs.service.webapp.repositories.impl.RoomRepositoryImpl
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class RoomRepositorySpec extends RepositorySpec {

  private val roomDataStore: RoomDataStore = mock[RoomDataStore]
  private val repository: RoomRepository = new RoomRepositoryImpl(roomDataStore)

  private val room = Room("Room 1")
  private val token = "token"

  private val roomCreationRequest = CreateRoomRequest("Room 1", user.username, token)
  private val deleteRoomRequest = DeleteRoomRequest(room.name, user.username, token)

  private val createRoomSubscriber: Subscriber[Room] = stub[Subscriber[Room]]
  private val deleteRoomSubscriber: Subscriber[String] = stub[Subscriber[String]]
  private val registerUserSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]

  it should "create a new room" in {
    // Given
    (roomDataStore createRoom _) expects roomCreationRequest returns Observable.just(room) noMoreThanOnce()

    // When
    repository createRoom roomCreationRequest subscribe createRoomSubscriber

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (createRoomSubscriber onNext _) verify room once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => createRoomSubscriber onCompleted) verify() once()
  }

  it should "delete an existing room" in {
    // Given
    (roomDataStore createRoom _) expects roomCreationRequest returns Observable.just(room) noMoreThanOnce()
    (roomDataStore deleteRoom _) expects deleteRoomRequest returns Observable.just(room.name) noMoreThanOnce()

    // When
    repository.createRoom(roomCreationRequest)
      .flatMap(_ => repository.deleteRoom(deleteRoomRequest))
      .subscribe(deleteRoomSubscriber)

    // Then
    (deleteRoomSubscriber onNext _) verify room.name once()
    (() => deleteRoomSubscriber onCompleted) verify() once()
  }

  it should "save a new user" in {
    // Given
    (roomDataStore registerUser _) expects registerRequest returns Observable.empty noMoreThanOnce()

    // When
    repository.registerUser(registerRequest).subscribe(registerUserSubscriber)

    // Then
    (() => registerUserSubscriber onCompleted) verify() once()
  }

}
