package it.unibo.dcs.commons

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

package object dataaccess {
  private val mySqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  object Implicits {

    implicit def dateToString(date: Date): String = mySqlFormat.format(date)

    implicit def stringToDate(date: String): Date = mySqlFormat.parse(date)

  }
}
