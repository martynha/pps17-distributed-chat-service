package it.unibo.dcs.commons.interactor

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import rx.lang.scala.Observable

/** This class can be used to check that a request follows all the rules
  * described in the validator
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param validator           object containing the rules needed to validate
  * @usecase check that a request follows all the rules */
abstract class Validation[R](private[this] val threadExecutor: ThreadExecutor,
                             private[this] val postExecutionThread: PostExecutionThread,
                             private[this] val validator: Validator[R])
  extends UseCase[Unit, R](threadExecutor, postExecutionThread) {

  override final protected[this] def createObservable(request: R): Observable[Unit] = validator.validate(request)

}
