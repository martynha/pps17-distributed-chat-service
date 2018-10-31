package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.JoinRoomRequest

/** An object of this class can be used to check that a login request is valid.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param validator           object containing the rules needed to validate
  * @usecase                   check that a join room request is valid */
final class JoinRoomValidation(private[this] val threadExecutor: ThreadExecutor,
                          private[this] val postExecutionThread: PostExecutionThread,
                          private[this] val validator: Validator[JoinRoomRequest])
  extends Validation[JoinRoomRequest](threadExecutor, postExecutionThread, validator)

object JoinRoomValidation {

  /** Factory method to instantiate the class
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param validator           object containing the rules needed to validate
    * @return                    an instantiation of the class */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            validator: Validator[JoinRoomRequest]): JoinRoomValidation =
    new JoinRoomValidation(threadExecutor, postExecutionThread, validator)
}
