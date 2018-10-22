package it.unibo.dcs.service.authentication

import it.unibo.dcs.service.authentication.authentication.Messages._
import it.unibo.dcs.commons.validation.{Conditions, Validator}
import it.unibo.dcs.exceptions.{MissingPasswordException, MissingTokenException, MissingUsernameException, UserNotFoundException}
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import it.unibo.dcs.service.authentication.request.{LoginUserRequest, LogoutUserRequest, RegisterUserRequest}

package object authentication {

  object LogoutUserValidator {
    def apply(authRepository: AuthenticationRepository): Validator[LogoutUserRequest] = Validator[LogoutUserRequest] {
      builder =>
        builder.addRule(builder.observableRule(request =>
          Conditions.stringNotEmpty(request.token), MissingTokenException(missingToken)))
    }
  }

  object RegistrationValidator {
    def apply(): Validator[RegisterUserRequest] = Validator[RegisterUserRequest] {
      builder =>
        builder
          .addRule(builder.observableRule(
            request => Conditions.stringNotEmpty(request.username),
            MissingUsernameException(missingUsername)))

          .addRule(builder.observableRule(
            request => request.password != null && !request.password.isEmpty,
            MissingPasswordException(missingPassword)))
    }
  }

  object LoginValidator {
    def apply(authRepository: AuthenticationRepository): Validator[LoginUserRequest] = Validator[LoginUserRequest] {
      builder =>
        builder
          .addRule(builder.observableRule(
            request => Conditions.stringNotEmpty(request.username),
            MissingUsernameException(missingUsername)
          ))

          .addRule(builder.observableRule(
            request => Conditions.stringNotEmpty(request.password),
            MissingPasswordException(missingPassword)
          ))

          .addRule(request => authRepository.checkUserExistence(request.username)
            .doOnError(throw UserNotFoundException(request.username)))
    }
  }

  object Messages {
    val missingUsername = "Username missing in registration request"
    val missingPassword = "Password missing in registration request"
    val missingToken = "Token missing in logout request"
  }

}


