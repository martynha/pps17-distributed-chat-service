package it.unibo.dcs.service.authentication

import it.unibo.dcs.commons.validation.{Conditions, Validator}
import it.unibo.dcs.exceptions.{PasswordRequiredException, TokenRequiredException, UserNotFoundException, UsernameRequiredException}
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import it.unibo.dcs.service.authentication.request.{CheckTokenRequest, LoginUserRequest, LogoutUserRequest, RegisterUserRequest}

package object validator {

  object LogoutUserValidator {
    def apply(authRepository: AuthenticationRepository): Validator[LogoutUserRequest] = Validator[LogoutUserRequest] {
      builder => builder.addRule(request =>
          Conditions.stringNotEmpty(request.token), TokenRequiredException
        )
    }
  }

  object RegistrationValidator {
    def apply(): Validator[RegisterUserRequest] = Validator[RegisterUserRequest] {
      builder => builder
          .addRule(
            request => Conditions.stringNotEmpty(request.username),
            UsernameRequiredException
          )
          .addRule(
            request => Conditions.stringNotEmpty(request.password),
            PasswordRequiredException
          )
    }
  }

  object LoginValidator {
    def apply(authRepository: AuthenticationRepository): Validator[LoginUserRequest] = Validator[LoginUserRequest] {
      builder => builder
          .addRule(
            request => Conditions.stringNotEmpty(request.username),
            UsernameRequiredException
          )
          .addRule(
            request => Conditions.stringNotEmpty(request.password),
            PasswordRequiredException
          )
          .addRule(request => authRepository.checkUserExistence(request.username))
    }
  }

  object CheckTokenValidator {
    def apply: Validator[CheckTokenRequest] = Validator[CheckTokenRequest] {
      builder => builder
          .addRule(
            request => Conditions.stringNotEmpty(request.token), TokenRequiredException)
    }
  }

}


