package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.usecases.JoinRoomUseCase
import it.unibo.dcs.service.room.model.Participation
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.JoinRoomRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class JoinRoomUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {
  val threadExecutor = mock[ThreadExecutor]
  val postExecutionThread = mock[PostExecutionThread]
  val roomRepository = mock[RoomRepository]
  val joinRoomUseCase = new JoinRoomUseCase(threadExecutor, postExecutionThread, roomRepository)

  val request = JoinRoomRequest("Aula Magna", "martynha")

  val subscriber = stub[Subscriber[Participation]]

  it should "create a new participation when the use case is executed" in {
    //Given
    (roomRepository joinRoom _ ) expects request returns Observable.just()

    //When
    joinRoomUseCase(request).subscribe(subscriber)

    //Then
    (() => subscriber.onCompleted) verify() once()
  }
}
