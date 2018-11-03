package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.{GetRoomsRequest, GetUserParticipationsRequest}

final class GetUserParticipationsValidation(private[this] val threadExecutor: ThreadExecutor,
                                            private[this] val postExecutionThread: PostExecutionThread,
                                            private[this] val validator: Validator[GetUserParticipationsRequest])
  extends Validation[GetUserParticipationsRequest](threadExecutor, postExecutionThread, validator)
