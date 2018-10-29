package it.unibo.dcs.commons

import rx.lang.scala.Observable

package object validation {

  type Rule[T] = T => Observable[Unit]

}
