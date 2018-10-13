package repositories.datastores

import java.util.Date

import it.unibo.dcs.service.webapp.interaction.Requests.CreateRoomRequest
import it.unibo.dcs.service.webapp.model.{Room, User}
import it.unibo.dcs.service.webapp.repositories.datastores.RoomDataStore
import it.unibo.dcs.service.webapp.repositories.datastores.api.RoomApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.RoomDataStoreNetwork
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class RoomDataStoreSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  private val roomApi: RoomApi = mock[RoomApi]
  private val dataStore: RoomDataStore = new RoomDataStoreNetwork(roomApi)
  private val user = User("niklegend", "nicola", "piscaglia", "bla", visible = true, new Date())
  private val room = Room("Room 1")
  private val token = "token"
  private val roomCreationRequest = CreateRoomRequest("Room 1", user, token)
  private val createRoomSubscriber: Subscriber[Room] = stub[Subscriber[Room]]

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
}
