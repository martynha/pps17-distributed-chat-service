package usecases

import it.unibo.dcs.service.webapp.interaction.Requests.LogoutUserRequest
import it.unibo.dcs.service.webapp.usecases.LogoutUserUseCase
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class LogoutUseCaseSpec extends UseCaseSpec {

  private val logoutUserRequest = LogoutUserRequest(user.username, token)

  private val logoutSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]

  private val logoutUseCase = new LogoutUserUseCase(threadExecutor, postExecutionThread, authRepository)


  it should "logout the user when the use case is executed" in {
    // Given
    (authRepository logoutUser _) expects logoutUserRequest returns Observable.empty

    // When
    logoutUseCase(logoutUserRequest) subscribe logoutSubscriber

    // Then
    (() => logoutSubscriber onCompleted) verify() once()
  }
}
