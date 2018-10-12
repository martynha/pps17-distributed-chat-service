package usecases

import java.util.Date

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.webapp.model.{Room, User}
import it.unibo.dcs.service.webapp.interaction.Requests.CreateRoomRequest
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository}
import it.unibo.dcs.service.webapp.usecases.CreateRoomUseCase
import it.unibo.dcs.service.webapp.interaction.Results.RoomCreationResult
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class CreateRoomUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {
  private val user = User("niklegend", "Nicola", "Piscaglia", "bio", visible = true, new Date)
  private val room = Room("Room 1")
  private val token = "token"
  private val createRoomRequest = CreateRoomRequest("Room 1", user, token)
  private val roomCreationResult = RoomCreationResult(room)

  private val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  private val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]
  private val roomRepository: RoomRepository = mock[RoomRepository]
  private val authRepository: AuthenticationRepository = mock[AuthenticationRepository]

  private val roomCreationSubscriber: Subscriber[RoomCreationResult] = stub[Subscriber[RoomCreationResult]]

  private val createRoomUseCase = new CreateRoomUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)


  it should "create a new room when the use case is executed" in {
    // Given
    (roomRepository createRoom _) expects createRoomRequest returns (Observable just room)
    // userRepository is called with `registerRequest` as parameter returns an observable that contains only `user`
    (authRepository createRoom _) expects createRoomRequest returns (Observable just token)

    // When
    // createUserUseCase is executed with argument `request`
    createRoomUseCase(createRoomRequest) subscribe roomCreationSubscriber

    // Then
    (roomCreationSubscriber onNext _) verify roomCreationResult once()
    (() => roomCreationSubscriber onCompleted) verify() once()
  }
}
