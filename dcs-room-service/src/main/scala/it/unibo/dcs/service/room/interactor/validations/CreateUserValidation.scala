package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.CreateUserRequest
import rx.lang.scala.Observable

final class CreateUserValidation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[CreateUserRequest])
  extends Validation[Unit, CreateUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateUserRequest): Observable[Unit] =
    validator.validate(request)
}
