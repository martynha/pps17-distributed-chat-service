package it.unibo.dcs.service.room

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.repository.RoomRepository
import org.scalamock.scalatest.MockFactory

object Mocks extends MockFactory {

  val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]

  val roomRepository: RoomRepository = mock[RoomRepository]

  val roomDataStore: RoomDataStore = mock[RoomDataStore]

}
