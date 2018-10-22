package it.unibo.dcs.service.authentication.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.authentication.request.LogoutUserRequest
import rx.lang.scala.Observable

final class LogoutUserValidation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[LogoutUserRequest])
  extends Validation[Unit, LogoutUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: LogoutUserRequest): Observable[Unit] =
    validator.validate(request)
}

object LogoutUserValidation {
  def apply(threadExecutor: ThreadExecutor,
            postExecutionThread: PostExecutionThread,
            validator: Validator[LogoutUserRequest]): LogoutUserValidation =
    new LogoutUserValidation(threadExecutor, postExecutionThread, validator)
}
