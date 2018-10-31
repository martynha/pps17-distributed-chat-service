package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.SendMessageValidation
import it.unibo.dcs.service.room.model.Message
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.SendMessageRequest
import rx.lang.scala.Observable

final class SendMessageUseCase (threadExecutor: ThreadExecutor,
                          postExecutionThread: PostExecutionThread,
                          private[this] val roomRepository: RoomRepository,
                          private[this] val validation: SendMessageValidation)
  extends UseCase[Message, SendMessageRequest](threadExecutor, postExecutionThread){

  override protected[this] def createObservable(request: SendMessageRequest): Observable[Message] = roomRepository.sendMessage(request)
}
