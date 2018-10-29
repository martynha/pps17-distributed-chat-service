package it.unibo.dcs.commons.validation

object Conditions {

  def stringNotEmpty(field: String): Boolean = field != null && field.nonEmpty

}
