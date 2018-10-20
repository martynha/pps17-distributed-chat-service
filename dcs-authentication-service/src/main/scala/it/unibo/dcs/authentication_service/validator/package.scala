package it.unibo.dcs.authentication_service

import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.authentication_service.request.{CheckTokenRequest, LoginUserRequest, LogoutUserRequest, RegisterUserRequest}
import it.unibo.dcs.authentication_service.validator.Messages._
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.exceptions.{MissingPasswordException, MissingTokenException, MissingUsernameException, UserNotFoundException}

package object validator {

  object LogoutUserValidator {
    def apply(authRepository: AuthenticationRepository): Validator[LogoutUserRequest] = Validator[LogoutUserRequest] {
      builder =>
        builder.addRule(builder.observableRule(request =>
          builder.Conditions.stringNotEmpty(request.token), MissingTokenException(missingToken)))
    }
  }


  object RegistrationValidator {
    def apply: Validator[RegisterUserRequest] = Validator[RegisterUserRequest] {
      builder =>
        builder
          .addRule(builder.observableRule(
            request => builder.Conditions.stringNotEmpty(request.username),
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
            request => builder.Conditions.stringNotEmpty(request.username),
            MissingUsernameException(missingUsername)
          ))

          .addRule(builder.observableRule(
            request => builder.Conditions.stringNotEmpty(request.password),
            MissingPasswordException(missingPassword)
          ))

          .addRule(request => authRepository.checkUserExistence(request.username)
            .doOnError(throw UserNotFoundException(request.username)))
    }
  }

  object CheckTokenValidator {
    def apply: Validator[CheckTokenRequest] = Validator[CheckTokenRequest] {
      builder =>
        builder
          .addRule(builder.observableRule(
            request => builder.Conditions.stringNotEmpty(request.token), MissingTokenException(missingToken)))
    }
  }

  object Messages {
    val missingUsername = "Username missing in registration request"
    val missingPassword = "Password missing in registration request"
    val missingToken = "Token missing in logout request"
  }

}


