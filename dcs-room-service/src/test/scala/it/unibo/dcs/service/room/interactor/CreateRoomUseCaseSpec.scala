package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.service.room.Mocks._
import it.unibo.dcs.service.room.interactor.usecases.CreateRoomUseCase
import it.unibo.dcs.service.room.interactor.validations.CreateRoomValidation
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.request.CreateRoomRequest
import it.unibo.dcs.service.room.validator.CreateRoomValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class CreateRoomUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  private val createRoomUseCase = {
    val validation = new CreateRoomValidation(threadExecutor, postExecutionThread, CreateRoomValidator())
    new CreateRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

  private val request = CreateRoomRequest("Test room", "martynha")
  private val expectedRoom = Room(request.name)

  private val subscriber = stub[Subscriber[Room]]

  it should "Create a room when the CreateRoomUseCase is execute" in {
    //Given
    (roomRepository createRoom _) expects request returns (Observable just expectedRoom)

    //When
    createRoomUseCase(request).subscribe(subscriber)

    //Then
    (subscriber onNext _) verify expectedRoom once()
    (() => subscriber onCompleted) verify() once()
  }

}
