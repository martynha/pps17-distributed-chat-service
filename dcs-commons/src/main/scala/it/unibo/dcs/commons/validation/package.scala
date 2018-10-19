package it.unibo.dcs.commons

import rx.lang.scala.Observable

package object validation {
  type Rule[A] = A => Observable[Unit]
}
