package it.unibo.dcs.service.authentication.interactor

import it.unibo.dcs.service.MocksForUseCases._
import _root_.it.unibo.dcs.service.authentication.interactor.usecases.LogoutUserUseCase
import _root_.it.unibo.dcs.service.authentication.request.Requests.LogoutUserRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

class LogoutUserUseCaseTest extends FlatSpec with MockFactory {

  private val token = "header.eyJzdWIiOiAiYWxlIn0=.signature"
  private val request = LogoutUserRequest(token)
  private val expectedResult: Unit = Unit

  private val subscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  private val logoutUserUseCase = new LogoutUserUseCase(threadExecutor, postExecutionThread, authRepository)

  it should "logout the user when the use case is executed" in {
    (authRepository invalidToken(_, _)) expects(token, *) returns (Observable just expectedResult)

    logoutUserUseCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}
