package it.unibo.dcs.service.room

import it.unibo.dcs.commons.validation.{Conditions, Validator}
import it.unibo.dcs.exceptions.{RoomNameRequiredException, UsernameRequiredException}
import it.unibo.dcs.service.room.request._

package object validator {

  object CreateUserValidator {
    def apply(): Validator[CreateUserRequest] = Validator[CreateUserRequest] {
      builder =>
        builder.addRule(request =>
          Conditions.stringNotEmpty(request.username),
          UsernameRequiredException)
    }
  }

  object CreateRoomValidator {
    def apply(): Validator[CreateRoomRequest] = Validator[CreateRoomRequest] {
      builder => builder
          .addRule(request =>
            Conditions.stringNotEmpty(request.name),
            RoomNameRequiredException
          )
          .addRule(request =>
            Conditions.stringNotEmpty(request.username),
            UsernameRequiredException
          )
    }
  }

  object DeleteRoomValidator {
    def apply(): Validator[DeleteRoomRequest] = Validator[DeleteRoomRequest] {
      builder => builder
          .addRule(request =>
            Conditions.stringNotEmpty(request.name),
            RoomNameRequiredException
          )
          .addRule(request =>
            Conditions.stringNotEmpty(request.username),
            UsernameRequiredException
          )
    }
  }

  object GetRoomsValidator {
    def apply(): Validator[GetRoomsRequest] = Validator[GetRoomsRequest] {
      builder => builder // no rules to specify
    }
  }

  object JoinRoomValidator {
    def apply(): Validator[JoinRoomRequest] = Validator[JoinRoomRequest] {
      builder => builder
          .addRule(request =>
            Conditions.stringNotEmpty(request.name),
            RoomNameRequiredException
          )
          .addRule(request =>
            Conditions.stringNotEmpty(request.username),
            UsernameRequiredException
          )
    }
  }
}
