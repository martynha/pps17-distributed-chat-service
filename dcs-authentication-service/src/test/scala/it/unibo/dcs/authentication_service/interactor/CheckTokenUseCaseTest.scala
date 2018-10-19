package it.unibo.dcs.authentication_service.interactor

import it.unibo.dcs.authentication_service.MocksForUseCases.{authRepository, postExecutionThread, threadExecutor}
import it.unibo.dcs.authentication_service.request.CheckTokenRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

class CheckTokenUseCaseTest extends FlatSpec with MockFactory {

  val token = "token"
  val request = CheckTokenRequest(token)
  val expectedResult = true

  val subscriber: Subscriber[Boolean] = stub[Subscriber[Boolean]]
  val useCase = new CheckTokenUseCase(threadExecutor, postExecutionThread, authRepository)

  it should "return true when the use case is executed" in {
    (authRepository isTokenValid _) expects request.token returns (Observable just expectedResult)
    useCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}