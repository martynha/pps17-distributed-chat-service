package it.unibo.dcs.service.authentication.interactor.validations

import it.unibo.dcs.commons.interactor.SimpleValidation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.authentication.request.Requests.LogoutUserRequest

/** An object of this class can be used to check that a logout request is valid.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param validator           object containing the rules needed to validate
  * @usecase                   check that a logout request is valid */
final class LogoutUserValidation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[LogoutUserRequest])
  extends SimpleValidation[LogoutUserRequest](threadExecutor, postExecutionThread, validator)

object LogoutUserValidation {

  /** Factory method to instantiate the class
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param validator           object containing the rules needed to validate
    * @return                    an instantiation of the class */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            validator: Validator[LogoutUserRequest]): LogoutUserValidation =
    new LogoutUserValidation(threadExecutor, postExecutionThread, validator)
}
