package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.LogoutUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.LogoutResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class LogoutUseCaseSpec extends UseCaseSpec {

  private val logoutUserRequest = LogoutUserRequest(user.username, token)

  private val logoutSubscriber: Subscriber[LogoutResult] = stub[Subscriber[LogoutResult]]

  private val logoutUseCase = new LogoutUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository)

  private val expectedResult = LogoutResult(user)

  it should "logout the user when the use case is executed" in {
    // Given
    (authRepository logoutUser _) expects logoutUserRequest returns Observable.just(Unit)
    (userRepository getUserByUsername  _) expects logoutUserRequest.username returns Observable.just(user)
    (userRepository updateAccess  _) expects logoutUserRequest.username returns Observable.just(Unit)

    // When
    logoutUseCase(logoutUserRequest) subscribe logoutSubscriber

    // Then
    (logoutSubscriber onNext _) verify expectedResult once()
    (() => logoutSubscriber onCompleted) verify() once()
  }
}
