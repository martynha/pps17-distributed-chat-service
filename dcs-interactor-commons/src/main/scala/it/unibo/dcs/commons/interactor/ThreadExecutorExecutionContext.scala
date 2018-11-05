package it.unibo.dcs.commons.interactor

import io.vertx.scala.core.Vertx
import it.unibo.dcs.commons.interactor.executor.ThreadExecutor
import it.unibo.dcs.commons.logging.Logging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

final class ThreadExecutorExecutionContext(vertx: Vertx) extends ThreadExecutor with Logging {

  override def execute(runnable: Runnable): Unit = vertx.executeBlocking[Unit](() => runnable.run()).onComplete {
    case Success(_) =>
    case Failure(cause) => reportFailure(cause)
  }

  override def reportFailure(cause: Throwable): Unit = {
    log.error("Failed executing on worker executor", cause)
  }

}

object ThreadExecutorExecutionContext {

  def apply(vertx: Vertx): ThreadExecutorExecutionContext = new ThreadExecutorExecutionContext(vertx)

}
