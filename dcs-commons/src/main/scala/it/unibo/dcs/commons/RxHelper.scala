package it.unibo.dcs.commons

import io.vertx.rx.java.{RxHelper => JRxHelper}
import io.vertx.scala.core.{Context, Vertx}
import it.unibo.dcs.commons.RxHelper.Implicits._
import it.unibo.dcs.commons.VertxHelper.Implicits._
import rx.lang.scala.{Observable, Scheduler}

object RxHelper {

  def scheduler(context: Context): Scheduler = JRxHelper.scheduler(context)

  def scheduler(vertx: Vertx): Scheduler = JRxHelper.scheduler(vertx)

  def blockingScheduler(vertx: Vertx, ordered: Boolean = DEFAULT_ORDERED): Scheduler = JRxHelper.blockingScheduler(vertx, ordered)

  private val DEFAULT_ORDERED: Boolean = false

  object Implicits {

    private[commons] implicit final class RxScheduler(val asJavaScheduler: rx.Scheduler) extends Scheduler

    private[commons] implicit final class RxObservable[T](val asJavaObservable: rx.Observable[T]) extends Observable[T]

    implicit def map[A, B](observable: Observable[A])(implicit f: A => B): Observable[B] = observable.map(f)

    implicit def mapToUnit[T](observable: Observable[T]): Observable[Unit] = observable.map(_ => ())

  }

}
