package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.{CreateRoomRequest}
import rx.lang.scala.Observable

final class CreateRoomUseCase(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread, private[this] val roomRepository: RoomRepository)
  extends UseCase[Unit, CreateRoomRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateRoomRequest): Observable[Unit] = roomRepository.createRoom(request)

}
