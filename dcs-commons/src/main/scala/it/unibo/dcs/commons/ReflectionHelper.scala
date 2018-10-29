package it.unibo.dcs.commons

import scala.reflect.ClassTag

object ReflectionHelper {

  def asClassOf[T](ct: ClassTag[T]): Class[T] = ct.runtimeClass.asInstanceOf[Class[T]]

}
