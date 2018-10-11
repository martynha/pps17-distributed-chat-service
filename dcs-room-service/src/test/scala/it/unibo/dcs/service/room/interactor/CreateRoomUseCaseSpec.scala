package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.CreateRoomRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class CreateRoomUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {
  val threadExecutor = mock[ThreadExecutor]
  val postExecutionThread = mock[PostExecutionThread]
  val roomRepository = mock[RoomRepository]
  val createRoomUseCase = new CreateRoomUseCase(threadExecutor, postExecutionThread, roomRepository)

  val request = CreateRoomRequest("Aula Magna", "aulamagna")

  val subscriber = stub[Subscriber[Unit]]

  it should "Create a room when the CreateRoomUseCase is execute" in {
    (roomRepository createRoom _) expects request returns Observable.just()

    createRoomUseCase(request).subscribe(subscriber)

    (subscriber.onCompleted: () => Unit) verify() once()
  }
}
