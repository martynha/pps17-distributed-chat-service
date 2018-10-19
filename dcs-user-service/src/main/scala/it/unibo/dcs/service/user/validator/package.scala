package it.unibo.dcs.service.user

import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.exceptions.{MissingUsernameException, UsernameAlreadyTaken}
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import it.unibo.dcs.service.user.validator.Implicits._

import scala.language.implicitConversions

package object validator {

  object UserCreationValidator {
    def apply(userRepository: UserRepository): Validator[CreateUserRequest] = Validator[CreateUserRequest] { builder =>
      builder
        .addRule(builder.observableRule(request => request != null,
          new NullPointerException("User creation request is null"))
        )
        .addRule(builder.observableRule(request =>
          request.firstName != null && !request.firstName.isEmpty,
          new IllegalArgumentException("User firstname is empty"))
        )
        .addRule(builder.observableRule(request =>
          request.lastName != null && !request.lastName.isEmpty, new IllegalArgumentException("User lastname is empty"))
        )
        .addRule(builder.observableRule(request =>
          request.username != null && !request.username.isEmpty, MissingUsernameException("Username is empty"))
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

}
