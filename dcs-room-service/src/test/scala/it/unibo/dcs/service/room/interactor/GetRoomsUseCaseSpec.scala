package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.service.room.Mocks._
import it.unibo.dcs.service.room.interactor.usecases.GetRoomsUseCase
import it.unibo.dcs.service.room.interactor.validations.GetRoomsValidation
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.request.GetRoomsRequest
import it.unibo.dcs.service.room.validator.GetRoomsValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class GetRoomsUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  val validation = new GetRoomsValidation(threadExecutor, postExecutionThread, GetRoomsValidator())
  private val getRoomsUseCase = new GetRoomsUseCase(threadExecutor, postExecutionThread, roomRepository, validation)

  private val expectedResult = List(Room("Room 01"), Room("Room 02"), Room("Room 03"))

  private val username = "nik"
  val request = GetRoomsRequest(username)

  val subscriber:Subscriber[List[Room]] = stub[Subscriber[List[Room]]]

  it should "Get all the rooms" in {
    (roomRepository getRooms _) expects request returns Observable.just(expectedResult)

    getRoomsUseCase(request).subscribe(subscriber)

    subscriber.onNext _ verify expectedResult
  }

}
