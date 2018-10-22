package it.unibo.dcs.service.authentication.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.authentication.request.LoginUserRequest
import rx.lang.scala.Observable

final class LoginUserValidation(private[this] val threadExecutor: ThreadExecutor,
                                private[this] val postExecutionThread: PostExecutionThread,
                                private[this] val validator: Validator[LoginUserRequest])
  extends Validation[Unit, LoginUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: LoginUserRequest): Observable[Unit] =
    validator.validate(request)
}

object LoginUserValidation {
  def apply(threadExecutor: ThreadExecutor,
            postExecutionThread: PostExecutionThread,
            validator: Validator[LoginUserRequest]): LoginUserValidation =
    new LoginUserValidation(threadExecutor, postExecutionThread, validator)
}
