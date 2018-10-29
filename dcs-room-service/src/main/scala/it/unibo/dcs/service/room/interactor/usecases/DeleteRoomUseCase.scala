package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.DeleteRoomValidation
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.DeleteRoomRequest
import rx.lang.scala.Observable

final class DeleteRoomUseCase(threadExecutor: ThreadExecutor,
                              postExecutionThread: PostExecutionThread,
                              private[this] val roomRepository: RoomRepository,
                              private[this] val deleteRoomValidation: DeleteRoomValidation)

  extends UseCase[String, DeleteRoomRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: DeleteRoomRequest): Observable[String] =
    deleteRoomValidation(request)
      .flatMap(_ => roomRepository.deleteRoom(request))

}
