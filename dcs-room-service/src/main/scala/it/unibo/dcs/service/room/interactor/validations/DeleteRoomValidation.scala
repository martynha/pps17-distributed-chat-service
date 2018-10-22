package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.DeleteRoomRequest
import rx.lang.scala.Observable

final class DeleteRoomValidation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[DeleteRoomRequest])
  extends Validation[Unit, DeleteRoomRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: DeleteRoomRequest): Observable[Unit] =
    validator.validate(request)
}
