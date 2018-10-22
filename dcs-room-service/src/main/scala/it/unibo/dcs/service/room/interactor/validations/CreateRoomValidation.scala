package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.CreateRoomRequest
import rx.lang.scala.Observable

final class CreateRoomValidation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[CreateRoomRequest])
  extends Validation[Unit, CreateRoomRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateRoomRequest): Observable[Unit] =
    validator.validate(request)
}


