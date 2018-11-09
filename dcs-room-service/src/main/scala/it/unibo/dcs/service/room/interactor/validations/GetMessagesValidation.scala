package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.GetMessagesRequest

/** An object of this class can be used to check that a get message request is valid.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param validator           object containing the rules needed to validate
  * @usecase                   check that a send message request is valid */
final class GetMessagesValidation(private[this] val threadExecutor: ThreadExecutor,
                                  private[this] val postExecutionThread: PostExecutionThread,
                                  private[this] val validator: Validator[GetMessagesRequest])
  extends Validation[GetMessagesRequest] (threadExecutor, postExecutionThread, validator)

object GetMessagesValidation {
  /** Factory method to instantiate the class
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param validator           object containing the rules needed to validate
    * @return an instantiation of the class */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            validator: Validator[GetMessagesRequest]): GetMessagesValidation =
    new GetMessagesValidation(threadExecutor, postExecutionThread, validator)
}