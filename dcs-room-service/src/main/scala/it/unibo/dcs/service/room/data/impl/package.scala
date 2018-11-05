package it.unibo.dcs.service.room.data

import java.util.Date

import scala.language.implicitConversions

package object impl {

  private[impl] final case class ParticipationDto(name: String, username: String, join_date: Date)

}
