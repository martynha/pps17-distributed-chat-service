package it.unibo.dcs.commons.validation

import it.unibo.dcs.commons.validation.ValidatorBuilder.EXCEPTION
import rx.lang.scala.Observable

import scala.collection.mutable

final class ValidatorBuilder[T] private[validation]() {

  private[this] var _rules = Option(mutable.Buffer[Rule[T]]())

  def addRule(rule: Rule[T]): ValidatorBuilder[T] = {
    rules += rule
    this
  }

  def observableRule(condition: T => Boolean, error: Throwable): Rule[T] = {
    entity =>
      Observable.just(entity)
        .filter(condition)
        .singleOption
        .map(opt => opt.getOrElse(throw error))
  }

  private[validation] def build: Validator[T] = {
    val validator = new ValidatorImpl[T](rules.toList)
    rules.clear()
    _rules = None
    validator
  }

  private[this] def rules = _rules.getOrElse(throw EXCEPTION)

}

object ValidatorBuilder {

  private[validation] val EXCEPTION = new IllegalStateException("builder has already been built")

}
