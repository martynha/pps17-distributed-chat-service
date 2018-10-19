package it.unibo.dcs.commons.interactor

import it.unibo.dcs.commons.interactor.executor.Implicits._
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import rx.lang.scala.{Observable, Subscriber}

abstract class Validation[Unit, P](private[this] val threadExecutor: ThreadExecutor,
                                   private[this] val postExecutionThread: PostExecutionThread)
  extends Interactor[Unit, P] {

  override final def apply(parameters: P, subscriber: Subscriber[Unit]) =
    createObservable(parameters)
      .subscribeOn(threadExecutor)
      .observeOn(postExecutionThread)
      .subscribe(subscriber)

  override final def apply(parameters: P): Observable[Unit] = createObservable(parameters)

  protected[this] def createObservable(parameters: P): Observable[Unit]

}
