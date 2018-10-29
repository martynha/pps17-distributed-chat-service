package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.SimpleValidation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.CreateUserRequest

final class CreateUserValidation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[CreateUserRequest])
  extends SimpleValidation[CreateUserRequest](threadExecutor, postExecutionThread, validator)
