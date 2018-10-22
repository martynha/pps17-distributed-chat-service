package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.CreateRoomRequest
import rx.lang.scala.Observable

final class CreateRoomUseCase(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread, private[this] val roomRepository: RoomRepository)
  extends UseCase[Room, CreateRoomRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateRoomRequest): Observable[Room] = roomRepository.createRoom(request)

}
