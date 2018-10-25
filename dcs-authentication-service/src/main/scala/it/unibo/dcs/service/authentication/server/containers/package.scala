package it.unibo.dcs.service.authentication.server

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.authentication.interactor.usecases._
import it.unibo.dcs.service.authentication.interactor.validations._
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import it.unibo.dcs.service.authentication.validator.{DeleteUserValidator, LoginValidator, LogoutUserValidator, RegistrationValidator}

package object containers {

  def createUseCases(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
                     authenticationRepository: AuthenticationRepository, jwtAuth: JWTAuth): UseCaseContainer = {
    val loginUseCase = LoginUserUseCase(threadExecutor, postExecutionThread, authenticationRepository, jwtAuth)
    val logoutUseCase = LogoutUserUseCase(threadExecutor, postExecutionThread, authenticationRepository)
    val registerUseCase = RegisterUserUseCase(threadExecutor, postExecutionThread, authenticationRepository, jwtAuth)
    val checkTokenUseCase = CheckTokenUseCase(threadExecutor, postExecutionThread, authenticationRepository)
    val deleteUserUseCase = DeleteUserUseCase(threadExecutor, postExecutionThread, authenticationRepository)
    UseCaseContainer(loginUseCase, logoutUseCase, registerUseCase, checkTokenUseCase, deleteUserUseCase)
  }

  def createValidations(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
                        authenticationRepository: AuthenticationRepository): ValidationContainer = {
    val logoutValidator = LogoutUserValidator(authenticationRepository)
    val logoutUserValidation = LogoutUserValidation(threadExecutor, postExecutionThread, logoutValidator)
    val registrationValidation = RegisterUserValidation(threadExecutor, postExecutionThread, RegistrationValidator())
    val loginValidator = LoginValidator(authenticationRepository)
    val loginValidation = LoginUserValidation(threadExecutor, postExecutionThread, loginValidator)
    val deleteUserValidator = DeleteUserValidator(authenticationRepository)
    val deleteUserValidation = DeleteUserValidation(threadExecutor, postExecutionThread, deleteUserValidator)
    ValidationContainer(logoutUserValidation, registrationValidation, loginValidation, deleteUserValidation)
  }

  case class UseCaseContainer(loginUserUseCase: LoginUserUseCase,
                              logoutUserUseCase: LogoutUserUseCase,
                              registerUserUseCase: RegisterUserUseCase,
                              checkTokenUseCase: CheckTokenUseCase,
                              deleteUserUseCase: DeleteUserUseCase)

  case class ValidationContainer(logoutUserValidation: LogoutUserValidation,
                                 registrationValidation: RegisterUserValidation,
                                 loginUserValidation: LoginUserValidation,
                                 deleteUserValidation: DeleteUserValidation)

}
