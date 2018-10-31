package it.unibo.dcs.service.authentication.interactor

import it.unibo.dcs.service.MocksForUseCases.{authRepository, postExecutionThread, threadExecutor}
import it.unibo.dcs.service.authentication.interactor.usecases.DeleteUserUseCase
import it.unibo.dcs.service.authentication.request.Requests.DeleteUserRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class DeleteUserUseCaseTest extends FlatSpec with MockFactory {

  private val username= "ale"
  private val token = "header.eyJzdWIiOiAiYWxlIn0=.signature"
  private val request = DeleteUserRequest(username, token)
  private val expectedResult: Unit = Unit

  private val subscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  private val deleteUserUseCase = new DeleteUserUseCase(threadExecutor, postExecutionThread, authRepository)

  it should "delete the user when the use case is executed" in {
    (authRepository deleteUser(_, _)) expects(username, token) returns (Observable just expectedResult)
    (authRepository invalidToken(_, _)) expects(token, *) returns (Observable just expectedResult)

    deleteUserUseCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}
