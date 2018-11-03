package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.GetRoomsValidation
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.GetRoomsRequest
import rx.lang.scala.Observable

final class GetRoomsUseCase(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
                            private[this] val roomRepository: RoomRepository,
                            private[this] val getRoomsValidation: GetRoomsValidation)
  extends UseCase[List[Room], GetRoomsRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetRoomsRequest): Observable[List[Room]] =
    getRoomsValidation(request).flatMap(_ => roomRepository.getRooms(request))

}
