package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.{GetRoomsValidation, GetUserParticipationsValidation}
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.{GetRoomsRequest, GetUserParticipationsRequest}
import rx.lang.scala.Observable

final class GetUserParticipationsUseCase(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
                                         private[this] val roomRepository: RoomRepository,
                                         private[this] val validation: GetUserParticipationsValidation)
  extends UseCase[List[Room], GetUserParticipationsRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetUserParticipationsRequest): Observable[List[Room]] =
    validation(request).flatMap(_ => roomRepository.getParticipationsByUsername(request))

}
