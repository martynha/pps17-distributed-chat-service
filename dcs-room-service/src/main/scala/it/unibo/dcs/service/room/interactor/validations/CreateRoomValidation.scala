package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.SimpleValidation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.CreateRoomRequest

final class CreateRoomValidation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[CreateRoomRequest])
  extends SimpleValidation[CreateRoomRequest](threadExecutor, postExecutionThread, validator)


