package it.unibo.dcs.service.user

import it.unibo.dcs.commons.validation.{Conditions, Validator}
import it.unibo.dcs.exceptions.{MissingFirstNameException, MissingLastNameException, MissingUsernameException, UsernameAlreadyTaken}
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import it.unibo.dcs.service.user.validator.Implicits._
import it.unibo.dcs.service.user.validator.Messages._

import scala.language.implicitConversions

package object validator {

  object UserCreationValidator {
    def apply(userRepository: UserRepository): Validator[CreateUserRequest] = Validator[CreateUserRequest] { builder =>
      builder
        .addRule(builder.observableRule(request => request != null,
          new NullPointerException(nullUserCreationRequest))
        )
        .addRule(builder.observableRule(request =>
          Conditions.stringNotEmpty(request.firstName), MissingFirstNameException(missingFirstNameInRegistration))
        )
        .addRule(builder.observableRule(request =>
          Conditions.stringNotEmpty(request.lastName), MissingLastNameException(missingLastNameInRegistration))
        )
        .addRule(builder.observableRule(request =>
          Conditions.stringNotEmpty(request.username), MissingUsernameException(missingUsernameInRegistration))
        )
        .addRule(request =>
          userRepository.getUserByUsername(request)
            .singleOption
            .map(opt => opt.flatMap(throw UsernameAlreadyTaken(request.username))))
    }
  }

  object Implicits {
    implicit def userCreationRequestToGetUserRequest(request: CreateUserRequest): GetUserRequest = {
      GetUserRequest(request.username)
    }
  }

  object Messages {
    val nullUserCreationRequest = "User creation request is null"
    val missingFirstNameInRegistration = "User firstname is empty"
    val missingUsernameInRegistration = "Username is empty"
    val missingLastNameInRegistration = "User lastname is empty"
  }

}
