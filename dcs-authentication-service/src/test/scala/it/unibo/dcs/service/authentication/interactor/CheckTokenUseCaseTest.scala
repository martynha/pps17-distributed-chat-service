package it.unibo.dcs.service.authentication.interactor

import it.unibo.dcs.service.MocksForUseCases.{authRepository, postExecutionThread, threadExecutor}
import it.unibo.dcs.service.authentication.interactor.usecases.CheckTokenUseCase
import it.unibo.dcs.service.authentication.request.Requests.CheckTokenRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class CheckTokenUseCaseTest extends FlatSpec with MockFactory {

  private val token = "token"
  private val request = CheckTokenRequest(token)
  private val expectedResult = true

  private val subscriber: Subscriber[Boolean] = stub[Subscriber[Boolean]]
  private val useCase = new CheckTokenUseCase(threadExecutor, postExecutionThread, authRepository)

  it should "return true when the use case is executed" in {
    (authRepository isTokenValid _) expects request.token returns (Observable just expectedResult)
    useCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}