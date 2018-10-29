package it.unibo.dcs.service.room.repository

import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.repository.impl.RoomRepositoryImpl
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest, GetRoomsRequest}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class RoomRepositorySpec extends FlatSpec with MockFactory with OneInstancePerTest {

  val roomDataStore = mock[RoomDataStore]
  val roomRepository = new RoomRepositoryImpl(roomDataStore)

  private val username = "mvandi"
  private val roomName = "Test room"
  private val rooms = Set(Room("Room 01"), Room("Room 02"))

  it should "create a new user on the data store" in {
    val request = CreateUserRequest(username)

    val subscriber = stub[Subscriber[Unit]]

    // Given
    (roomDataStore createUser _) expects request returns Observable.just()

    // When
    roomRepository.createUser(request).subscribe(subscriber)

    // Then
    (() => subscriber.onCompleted) verify() once()
  }

  it should "Create a new room on the data store" in {
    val request = CreateRoomRequest(roomName, username)

    val subscriber = stub[Subscriber[Room]]

    //Given
    (roomDataStore createRoom _) expects request returns Observable.just()

    roomRepository.createRoom(request).subscribe(subscriber)

    //Then
    (() => subscriber.onCompleted()) verify() once()
  }

  it should "Delete a room if the user who is trying to delete the room is also the user who created the room" in {
    val request = DeleteRoomRequest(roomName, username)

    val subscriber = stub[Subscriber[String]]

    // Given
    (roomDataStore deleteRoom _) expects request returns Observable.just(roomName)

    // When
    roomRepository.deleteRoom(request).subscribe(subscriber)

    // Then
    subscriber.onNext _ verify roomName once()
  }

  it should "Get all the rooms on the data store" in {
    val request = GetRoomsRequest()

    val subscriber = stub[Subscriber[Set[Room]]]

    //Given
    (roomDataStore getRooms _) expects request returns Observable.just(rooms)

    roomRepository.getRooms(request).subscribe(subscriber)

    //Then
    subscriber.onNext _ verify rooms once()
  }

}
