package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.usecases.DeleteRoomUseCase
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.DeleteRoomRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class DeleteRoomUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  val threadExecutor = mock[ThreadExecutor]
  val postExecutionThread = mock[PostExecutionThread]
  val roomRepository = mock[RoomRepository]
  val deleteRoomUseCase = new DeleteRoomUseCase(threadExecutor, postExecutionThread, roomRepository)

  val request = DeleteRoomRequest("Test room", "mvandi")

  val subscriber = stub[Subscriber[Unit]]

  it should "Delete a room if the user who is trying to delete the room is also the user who created the room" in {
    (roomRepository deleteRoom _) expects request returns Observable.just()

    deleteRoomUseCase(request).subscribe(subscriber)

    (subscriber.onCompleted: () => Unit) verify() once()
  }

}
