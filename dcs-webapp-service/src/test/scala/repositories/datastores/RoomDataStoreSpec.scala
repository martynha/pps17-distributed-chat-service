package repositories.datastores

import java.util.Date

import it.unibo.dcs.service.webapp.interaction.Requests.{CreateRoomRequest, DeleteRoomRequest, RoomJoinRequest, GetRoomsRequest}
import it.unibo.dcs.service.webapp.model.{Participation, Room}
import it.unibo.dcs.service.webapp.repositories.datastores.RoomDataStore
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.RoomDataStoreNetwork
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class RoomDataStoreSpec extends DataStoreSpec {

  private val roomApi: RoomApi = mock[RoomApi]
  private val dataStore: RoomDataStore = new RoomDataStoreNetwork(roomApi)

  private val room = Room("Room 1")
  private val rooms: List[Room]  = List(room, room, room)
  private val token = "token"
  private val participation = Participation(new Date(), room, user)

  private val roomCreationRequest = CreateRoomRequest("Room 1", user.username, token)
  private val roomDeletionRequest = DeleteRoomRequest(room.name, user.username, token)
  private val getRoomsRequest = GetRoomsRequest("martynha", token)
  private val joinRoomRequest = RoomJoinRequest(room.name, user.username, token)

  private val deleteRoomSubscriber = stub[Subscriber[String]]
  private val createRoomSubscriber = stub[Subscriber[Room]]
  private val registrationSubscriber = stub[Subscriber[Unit]]
  private val getRoomsSubscriber: Subscriber[List[Room]] = stub[Subscriber[List[Room]]]
  private val joinRoomSubscriber = stub[Subscriber[Participation]]

  it should "create a new room" in {
    // Given
    (roomApi createRoom _) expects roomCreationRequest returns Observable.just(room) noMoreThanOnce()

    // When
    dataStore.createRoom(roomCreationRequest).subscribe(createRoomSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (createRoomSubscriber onNext _) verify room once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => createRoomSubscriber onCompleted) verify() once()
  }

  it should "create a new participation when a user join a room" in {
    // Given
    (roomApi joinRoom _) expects joinRoomRequest returns Observable.just(participation) once()

    // When
    dataStore.joinRoom(joinRoomRequest).subscribe(joinRoomSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (joinRoomSubscriber onNext _) verify participation once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => joinRoomSubscriber onCompleted) verify() once()
  }

  it should "save a new user" in {
    // Given
    (roomApi registerUser _) expects registerRequest returns Observable.empty

    // When
    dataStore.registerUser(registerRequest).subscribe(registrationSubscriber)

    // Then
    (() => registrationSubscriber onCompleted) verify() once()
  }

  it should "delete an existing room" in {
    // Given
    (roomApi createRoom _) expects roomCreationRequest returns Observable.just(room) noMoreThanOnce()
    (roomApi deleteRoom _) expects roomDeletionRequest returns Observable.just(room.name) noMoreThanOnce()

    // When
    dataStore.createRoom(roomCreationRequest)
      .flatMap(_ => dataStore.deleteRoom(roomDeletionRequest))
      .subscribe(deleteRoomSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (deleteRoomSubscriber onNext _) verify room.name once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => deleteRoomSubscriber onCompleted) verify() once()
  }

  it should "gets a list of rooms" in {
    //Given
    (roomApi getRooms _) expects getRoomsRequest returns Observable.just(rooms)

    //When
    dataStore getRooms getRoomsRequest subscribe getRoomsSubscriber

    //Then
    //Verify that 'suscriber.onNext' has been callen once
    (getRoomsSubscriber onNext _) verify rooms once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => getRoomsSubscriber onCompleted) verify() once()
    //
  }
}
