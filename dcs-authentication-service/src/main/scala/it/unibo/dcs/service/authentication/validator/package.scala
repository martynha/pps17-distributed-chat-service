package it.unibo.dcs.service.authentication

import it.unibo.dcs.commons.validation.{Conditions, Validator}
import it.unibo.dcs.exceptions.{InvalidTokenException, PasswordRequiredException, TokenRequiredException, UsernameRequiredException}
import it.unibo.dcs.service.authentication.business_logic.JwtTokenDecoder
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import it.unibo.dcs.service.authentication.request.Requests._

package object validator {

  val jwtTokenDecoder = JwtTokenDecoder()

  object LogoutUserValidator {
    def apply(authRepository: AuthenticationRepository): Validator[LogoutUserRequest] = Validator[LogoutUserRequest] {
      _.addRule(request => Conditions.stringNotEmpty(request.token), TokenRequiredException)
    }
  }

  object DeleteUserValidator {
    def apply(authRepository: AuthenticationRepository): Validator[DeleteUserRequest] = Validator[DeleteUserRequest] {
      _
        .addRule(request => Conditions.stringNotEmpty(request.username), UsernameRequiredException)
        .addRule(request => jwtTokenDecoder.getUsernameFromToken(request.token) equals request.username,
          InvalidTokenException)
    }
  }

  object RegistrationValidator {
    def apply(): Validator[RegisterUserRequest] = Validator[RegisterUserRequest] {
      _
        .addRule(request => Conditions.stringNotEmpty(request.username), UsernameRequiredException)
        .addRule(request => Conditions.stringNotEmpty(request.password), PasswordRequiredException)
    }
  }

  object LoginValidator {
    def apply(authRepository: AuthenticationRepository): Validator[LoginUserRequest] = Validator[LoginUserRequest] {
      _
        .addRule(request => Conditions.stringNotEmpty(request.username), UsernameRequiredException)
        .addRule(request => Conditions.stringNotEmpty(request.password), PasswordRequiredException)
        .addRule(request => authRepository.checkUserExistence(request.username))
    }
  }

  object CheckTokenValidator {
    def apply: Validator[CheckTokenRequest] = Validator[CheckTokenRequest] {
      _
        .addRule(request => Conditions.stringNotEmpty(request.token), TokenRequiredException)
        .addRule(request => request.username.equals(JwtTokenDecoder().getUsernameFromToken(request.username)),
          InvalidTokenException)
    }
  }

}


