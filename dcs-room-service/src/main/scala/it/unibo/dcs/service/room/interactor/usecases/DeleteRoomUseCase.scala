package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.DeleteRoomRequest
import rx.lang.scala.Observable

final class DeleteRoomUseCase(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread, private[this] val roomRepository: RoomRepository)

  extends UseCase[Unit, DeleteRoomRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: DeleteRoomRequest): Observable[Unit] = roomRepository.deleteRoom(request)

}
