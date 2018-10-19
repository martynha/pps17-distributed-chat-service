package it.unibo.dcs.commons.interactor

import rx.lang.scala.Scheduler
import rx.lang.scala.schedulers.ExecutionContextScheduler

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

package object executor {

  trait ThreadExecutor extends ExecutionContext

  trait PostExecutionThread {

    def scheduler: Scheduler

  }

  object PostExecutionThread {

    def apply(scheduler: Scheduler): PostExecutionThread = DefaultPostExecutionThread(scheduler)

    private final case class DefaultPostExecutionThread(override val scheduler: Scheduler) extends PostExecutionThread

  }

  private[interactor] object Implicits {

    implicit def executionContextToScheduler(te: ThreadExecutor): Scheduler = ExecutionContextScheduler(te)

    implicit def postExecutionThreadToScheduler(pet: PostExecutionThread): Scheduler = pet.scheduler

  }

}
