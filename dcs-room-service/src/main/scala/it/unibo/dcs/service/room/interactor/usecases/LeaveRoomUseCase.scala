package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.LeaveRoomValidation
import it.unibo.dcs.service.room.model.Participation
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.LeaveRoomRequest
import rx.lang.scala.Observable

class LeaveRoomUseCase (threadExecutor: ThreadExecutor,
                       postExecutionThread: PostExecutionThread,
                       private[this] val roomRepository: RoomRepository,
                       private[this] val validation: LeaveRoomValidation)
  extends UseCase[Participation, LeaveRoomRequest](threadExecutor, postExecutionThread){

  override protected[this] def createObservable(request: LeaveRoomRequest): Observable[Participation] =
    validation(request).flatMap(_ => roomRepository.leaveRoom(request))

}
