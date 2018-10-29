package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.usecases.CreateRoomUseCase
import it.unibo.dcs.service.room.interactor.validations.CreateRoomValidation
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.CreateRoomRequest
import it.unibo.dcs.service.room.validator.CreateRoomValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class CreateRoomUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {
  val threadExecutor = mock[ThreadExecutor]
  val postExecutionThread = mock[PostExecutionThread]
  val roomRepository = mock[RoomRepository]

  val validation = new CreateRoomValidation(threadExecutor, postExecutionThread, CreateRoomValidator())
  val createRoomUseCase = new CreateRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)

  val name = "Aula Magna"
  val request = CreateRoomRequest(name, "martynha")
  val expectedRoom = Room(name)

  val subscriber = stub[Subscriber[Room]]

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
