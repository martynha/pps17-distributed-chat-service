package it.unibo.dcs.service.authentication.interactor

import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.auth.jwt.JWTOptions
import it.unibo.dcs.service.MocksForUseCases._
import _root_.it.unibo.dcs.service.authentication.interactor.usecases.RegisterUserUseCase
import _root_.it.unibo.dcs.service.authentication.request.Requests.RegisterUserRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class RegisterUserUseCaseTest extends FlatSpec with MockFactory {

  private val request = RegisterUserRequest("ale", "123456")
  private val expectedResult = "token"

  private val subscriber: Subscriber[String] = stub[Subscriber[String]]
  private val registerUserUseCase = new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository, jwtAuth)

  it should "register the user when the use case is executed" in {
    (authRepository createUser(_, _)) expects(request.username, request.password) returns (Observable just expectedResult)
    (jwtAuth generateToken(_: JsonObject, _: JWTOptions)) expects(*, *) returns expectedResult

    registerUserUseCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}