package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.service.room.Mocks._
import it.unibo.dcs.service.room.interactor.usecases.GetUserParticipationsUseCase
import it.unibo.dcs.service.room.interactor.validations.GetUserParticipationsValidation
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.request.GetUserParticipationsRequest
import it.unibo.dcs.service.room.validator.GetUserParticipationsValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

class GetUserParticipationsUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  private val getUserParticipationsUseCase = {
    val validation = new GetUserParticipationsValidation(threadExecutor, postExecutionThread, GetUserParticipationsValidator())
    new GetUserParticipationsUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

  private val request = GetUserParticipationsRequest("mvandi")

  private val expectedRooms = List(Room("Room1"), Room("Room2"))

  private val subscriber = stub[Subscriber[List[Room]]]

  it should "retrieve all the participations for the given user" in {
    //Given
    (roomRepository getParticipationsByUsername  _ ) expects request returns (Observable just expectedRooms)

    //When
    getUserParticipationsUseCase(request).subscribe(subscriber)

    //Then
    (subscriber onNext _) verify expectedRooms once()
    (() => subscriber onCompleted) verify() once()
  }

}
