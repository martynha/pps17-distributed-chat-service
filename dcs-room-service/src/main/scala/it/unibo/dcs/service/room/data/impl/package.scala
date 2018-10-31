package it.unibo.dcs.service.room.data

import java.util.Date

import it.unibo.dcs.service.room.model.{Participation, Room}

import scala.language.implicitConversions

package object impl {

  private[impl] final case class ParticipationDto(room: Room, username: String, join_date: Date)

  private[impl] object Implicits {

    implicit def participationDtoToParticipation(dto: ParticipationDto): Participation =
      Participation(
        room = dto.room,
        username = dto.username,
        joinDate = dto.join_date
      )

  }

}
