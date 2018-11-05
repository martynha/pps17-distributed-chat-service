package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.LeaveRoomRequest

/** An object of this class can be used to check that a leave room request is valid.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param validator           object containing the rules needed to validate
  * @usecase                   check that a leave room request is valid */
final class LeaveRoomValidation(private[this] val threadExecutor: ThreadExecutor,
                               private[this] val postExecutionThread: PostExecutionThread,
                               private[this] val validator: Validator[LeaveRoomRequest])
  extends Validation[LeaveRoomRequest](threadExecutor, postExecutionThread, validator)

object LeaveRoomValidation {

  /** Factory method to instantiate the class
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param validator           object containing the rules needed to validate
    * @return                    an instantiation of the class */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            validator: Validator[LeaveRoomRequest]): LeaveRoomValidation =
    new LeaveRoomValidation(threadExecutor, postExecutionThread, validator)
}
