package it.unibo.dcs.service.user

import it.unibo.dcs.commons.validation.{Conditions, Validator}
import it.unibo.dcs.exceptions.{FirstNameRequiredException, LastNameRequiredException, UsernameRequiredException}
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import it.unibo.dcs.service.user.validator.Implicits.userCreationRequestToGetUserRequest

import scala.language.implicitConversions

package object validator {

  object UserCreationValidator {
    def apply(userRepository: UserRepository): Validator[CreateUserRequest] = Validator[CreateUserRequest] {
      builder => builder
        .addRule(request =>
          Conditions.stringNotEmpty(request.firstName),
          FirstNameRequiredException
        )
        .addRule(request =>
          Conditions.stringNotEmpty(request.lastName), LastNameRequiredException
        )
        .addRule(request =>
          Conditions.stringNotEmpty(request.username), UsernameRequiredException
        )
        .addRule(request =>
          userRepository.checkIfUserExists(request))
    }
  }

  object Implicits {
    implicit def userCreationRequestToGetUserRequest(request: CreateUserRequest): GetUserRequest = {
      GetUserRequest(request.username)
    }
  }

}
