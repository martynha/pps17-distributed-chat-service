package it.unibo.dcs.service.user

import it.unibo.dcs.commons.validation.{Conditions, Validator, ValidatorBuilder}
import it.unibo.dcs.exceptions.{FirstNameRequiredException, LastNameRequiredException, UsernameRequiredException}
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.{CreateOrEditUserRequest, CreateUserRequest, EditUserRequest, GetUserRequest}
import it.unibo.dcs.service.user.validator.Implicits.userRequestToGetUserRequest

import scala.language.implicitConversions

package object validator {

  object UserCreationValidator {
    def apply(userRepository: UserRepository): Validator[CreateUserRequest] = Validator[CreateUserRequest] {
      builder =>
        applyCommonRules(builder, userRepository)
        .addRule(request =>
          userRepository.checkIfUserExists(request))
    }
  }

  object UserEditingValidator {
    def apply(userRepository: UserRepository): Validator[EditUserRequest] = Validator[EditUserRequest] {
      builder => applyCommonRules(builder, userRepository)
    }
  }

  private def applyCommonRules[T<:CreateOrEditUserRequest](builder: ValidatorBuilder[T],
                                                           userRepository: UserRepository): ValidatorBuilder[T] = {
    builder
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
  }

  object Implicits {
    implicit def userRequestToGetUserRequest(request: CreateOrEditUserRequest): GetUserRequest = {
      GetUserRequest(request.username)
    }
  }

}
