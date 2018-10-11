package it.unibo.dcs.service.room.repository

import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.repository.impl.RoomRepositoryImpl
import it.unibo.dcs.service.room.request.{CreateUserRequest, DeleteRoomRequest}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class RoomRepositorySpec extends FlatSpec with MockFactory with OneInstancePerTest {

  val roomDataStore = mock[RoomDataStore]
  val roomRepository = new RoomRepositoryImpl(roomDataStore)

  val subscriber = stub[Subscriber[Unit]]

  it should "create a new user on the data store" in {
    val request = CreateUserRequest("mvandi")

    // Given
    (roomDataStore createUser _) expects request returns Observable.just()

    // When
    roomRepository.createUser(request).subscribe(subscriber)

    // Then
    (() => subscriber.onCompleted) verify() once()
  }

  it should "Delete a room if the user who is trying to delete the room is also the user who created the room" in {
    val request = DeleteRoomRequest("Test room", "mvandi")

    // Given
    (roomDataStore deleteRoom _) expects request returns Observable.just()

    // When
    roomRepository.deleteRoom(request).subscribe(subscriber)

    // Then
    (() => subscriber.onCompleted) verify() once()
  }

}
