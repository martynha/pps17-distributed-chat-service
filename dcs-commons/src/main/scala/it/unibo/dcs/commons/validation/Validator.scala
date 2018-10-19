package it.unibo.dcs.commons.validation

import rx.lang.scala.Observable

trait Validator[T] {

  def validate(t: T): Observable[Unit]

}

object Validator {

  def apply[T](f: ValidatorBuilder[T] => Unit): Validator[T] = {
    val builder = new ValidatorBuilder[T]()
    f(builder)
    builder.build
  }

}
