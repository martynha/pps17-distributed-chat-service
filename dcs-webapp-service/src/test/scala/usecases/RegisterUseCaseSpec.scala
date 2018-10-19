package usecases

import java.util.Date

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.RegisterResult
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository, UserRepository}
import it.unibo.dcs.service.webapp.usecases.RegisterUserUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class RegisterUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  private val user = User("niklegend", "Nicola", "Piscaglia", "bio", visible = true, new Date)
  private val registerRequest = RegisterUserRequest(user.username, user.firstName, user.lastName, "password", "password")

  private val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  private val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]
  private val userRepository: UserRepository = mock[UserRepository]
  private val authRepository: AuthenticationRepository = mock[AuthenticationRepository]
  private val roomRepository: RoomRepository = mock[RoomRepository]

  private val token: String = "token"
  private val registerResult: RegisterResult = RegisterResult(user, token)

  private val registerSubscriber: Subscriber[RegisterResult] = stub[Subscriber[RegisterResult]]

  private val registerUseCase = new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository,
    userRepository, roomRepository)


  it should "register the user when the use case is executed" in {
    // Given
    (authRepository registerUser _) expects registerRequest returns (Observable just token)
    // userRepository is called with `registerRequest` as parameter returns an observable that contains only `user`
    (userRepository registerUser _) expects registerRequest returns (Observable just user)
    (roomRepository registerUser _) expects registerRequest returns (Observable empty)

    // When
    // createUserUseCase is executed with argument `request`
    registerUseCase(registerRequest) subscribe registerSubscriber

    // Then
    (registerSubscriber onNext _) verify registerResult once()
    (() => registerSubscriber onCompleted) verify() once()
  }
}
