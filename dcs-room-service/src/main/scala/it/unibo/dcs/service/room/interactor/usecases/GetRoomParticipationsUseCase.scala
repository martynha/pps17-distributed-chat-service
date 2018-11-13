package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.GetRoomParticipationsValidation
import it.unibo.dcs.service.room.model.Participation
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.GetRoomParticipationsRequest
import rx.lang.scala.Observable

final class GetRoomParticipationsUseCase(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
                                         private[this] val roomRepository: RoomRepository,
                                         private[this] val getParticipationsValidation: GetRoomParticipationsValidation)
  extends UseCase[List[Participation], GetRoomParticipationsRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetRoomParticipationsRequest): Observable[List[Participation]] =
    getParticipationsValidation(request)
      .flatMap(_ => roomRepository.getRoomParticipations(request))
}
