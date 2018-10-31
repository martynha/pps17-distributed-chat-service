package it.unibo.dcs.service.user.data

import java.util.Date

import it.unibo.dcs.commons.dataaccess.Implicits.stringToBoolean
import it.unibo.dcs.service.user.model.User

import scala.language.implicitConversions

package object impl {

  private[impl] final case class UserDto(username: String, first_name: String, last_name: String, bio: String, visible: String, last_seen: Date)

  private[impl] object Implicits {
    implicit def userDtoToUser(dto: UserDto): User = User(
      username = dto.username,
      firstName = dto.first_name,
      lastName = dto.last_name,
      bio = dto.bio,
      visible = dto.visible,
      lastSeen = dto.last_seen
    )
  }

}
