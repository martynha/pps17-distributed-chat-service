package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.SimpleValidation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.GetRoomsRequest

final class GetRoomsValidation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[GetRoomsRequest])
  extends SimpleValidation[GetRoomsRequest](threadExecutor, postExecutionThread, validator)
