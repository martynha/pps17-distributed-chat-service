package it.unibo.dcs.service.user.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.user.request.EditUserRequest

final class ValidateUserEditing(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[EditUserRequest])
  extends Validation[EditUserRequest](threadExecutor, postExecutionThread, validator)
