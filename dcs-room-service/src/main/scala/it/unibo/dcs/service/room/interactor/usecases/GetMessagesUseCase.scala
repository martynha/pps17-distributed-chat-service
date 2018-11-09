package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.GetMessagesValidation
import it.unibo.dcs.service.room.model.Message
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.GetMessagesRequest
import rx.lang.scala.Observable

class GetMessagesUseCase(threadExecutor: ThreadExecutor,
                         postExecutionThread: PostExecutionThread,
                         private[this] val roomRepository: RoomRepository,
                         private[this] val validation: GetMessagesValidation)
  extends UseCase[List[Message], GetMessagesRequest](threadExecutor, postExecutionThread){

  override protected[this] def createObservable(request: GetMessagesRequest): Observable[List[Message]] = roomRepository.getMessages(request)
}
