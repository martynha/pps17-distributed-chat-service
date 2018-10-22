package it.unibo.dcs.service.room

import it.unibo.dcs.commons.validation.{Conditions, Validator}
import it.unibo.dcs.exceptions.{MissingRoomNameException, MissingUsernameException}
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest}
import it.unibo.dcs.service.room.validator.Messages._

package object validator {

  object CreateUserValidator {
    def apply(): Validator[CreateUserRequest] = Validator[CreateUserRequest] {
      builder =>
        builder.addRule(builder.observableRule(request =>
          Conditions.stringNotEmpty(request.username), MissingUsernameException(missingUsernameInRegistration)))
    }
  }

  object CreateRoomValidator {
    def apply(): Validator[CreateRoomRequest] = Validator[CreateRoomRequest] {
      builder =>
        builder
          .addRule(builder.observableRule(request =>
            Conditions.stringNotEmpty(request.name), MissingRoomNameException(missingRoomNameInCreation)))

          .addRule(builder.observableRule(request =>
            Conditions.stringNotEmpty(request.username), MissingUsernameException(missingCreatorInCreation)))
    }
  }

  object DeleteRoomValidator {
    def apply(): Validator[DeleteRoomRequest] = Validator[DeleteRoomRequest] {
      builder =>
        builder
          .addRule(builder.observableRule(request =>
            Conditions.stringNotEmpty(request.name), MissingRoomNameException(missingRoomNameInDeletion)))

          .addRule(builder.observableRule(request =>
            Conditions.stringNotEmpty(request.username), MissingUsernameException(missingCreatorInDeletion)))
    }
  }

  object Messages {
    val missingUsernameInRegistration = "Username missing in user creation request"
    val missingRoomNameInCreation = "Room name missing in room creation request"
    val missingCreatorInCreation = "Creator username missing in room creation request"
    val missingCreatorInDeletion = "Creator username missing in room deletion request"
    val missingRoomNameInDeletion = "Room name missing in room deletion request"
  }

}
