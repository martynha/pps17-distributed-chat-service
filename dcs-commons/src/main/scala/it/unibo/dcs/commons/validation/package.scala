package it.unibo.dcs.commons

import it.unibo.dcs.commons.RxHelper.Implicits.RichObservable
import rx.lang.scala.Observable

package object validation {

  type Rule[T] = T => Observable[Unit]

  sealed trait Validator[T] {

    def validate(t: T): Observable[Unit]

  }

  object Validator {

    def apply[T](f: ValidatorBuilder[T] => Unit): Validator[T] = {
      val builder = new ValidatorBuilder[T]()
      f(builder)
      builder.build
    }

  }

  private[validation] final class ValidatorImpl[T](private[this] val rules: List[Rule[T]]) extends Validator[T] {

    override def validate(t: T): Observable[Unit] = Observable.from(rules)
      .flatMap(_(t))
      .toCompletable

  }

}
