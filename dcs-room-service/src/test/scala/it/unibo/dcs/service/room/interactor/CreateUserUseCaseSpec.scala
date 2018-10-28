package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.usecases.CreateUserUseCase
import it.unibo.dcs.service.room.interactor.validations.CreateUserValidation
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.CreateUserRequest
import it.unibo.dcs.service.room.validator.CreateUserValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class CreateUserUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  val threadExecutor = mock[ThreadExecutor]
  val postExecutionThread = mock[PostExecutionThread]
  val roomRepository = mock[RoomRepository]

  val validation = new CreateUserValidation(threadExecutor, postExecutionThread, CreateUserValidator())
  val createUserUseCase = new CreateUserUseCase(threadExecutor, postExecutionThread, roomRepository, validation)

  val subscriber: Subscriber[Unit] = stub[Subscriber[Unit]]

  val request = CreateUserRequest("mvandi")

  it should "create a new user when the use case is executed" in {
    // Given
    (roomRepository createUser _) expects request returns Observable.just()

    // When
    createUserUseCase(request).subscribe(subscriber)

    // Then
    (() => subscriber onCompleted) verify() once()
  }

}
