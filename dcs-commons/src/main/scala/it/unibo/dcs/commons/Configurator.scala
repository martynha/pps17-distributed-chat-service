package it.unibo.dcs.commons

import Configurator.toOp

class Configurator[T] private (private[this] val op: T => T) {

  def andThen(f: T => Unit): Configurator[T] = new Configurator[T](op andThen toOp(f))

  def apply(t: T): T = op(t)

}

object Configurator {

  def apply[T]: Configurator[T] = new Configurator[T](identity)

  private def toOp[T](f: T => Unit): T => T = { t => f(t); t }

}
