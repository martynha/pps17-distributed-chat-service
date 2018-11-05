package it.unibo.dcs.service.authentication.server

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.authentication.interactor.usecases._
import it.unibo.dcs.service.authentication.interactor.validations._
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import it.unibo.dcs.service.authentication.validator._

package object containers {

  def createUseCases(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
                     authenticationRepository: AuthenticationRepository, jwtAuth: JWTAuth,
                     validations: ValidationContainer): UseCaseContainer = {

    val loginUseCase = LoginUserUseCase(threadExecutor, postExecutionThread, authenticationRepository,
      jwtAuth, validations.loginUserValidation)
    val logoutUseCase = LogoutUserUseCase(threadExecutor, postExecutionThread, authenticationRepository,
      validations.logoutUserValidation)
    val registerUseCase = RegisterUserUseCase(threadExecutor, postExecutionThread, authenticationRepository,
      jwtAuth, validations.registerUserValidation)
    val checkTokenUseCase = CheckTokenUseCase(threadExecutor, postExecutionThread, authenticationRepository,
      validations.checkTokenValidation)
    val deleteUserUseCase = DeleteUserUseCase(threadExecutor, postExecutionThread, authenticationRepository,
      validations.deleteUserValidation)

    UseCaseContainer(loginUseCase, logoutUseCase, registerUseCase, checkTokenUseCase, deleteUserUseCase)
  }

  def createValidations(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
                        authenticationRepository: AuthenticationRepository): ValidationContainer = {

    val logoutUserValidation = LogoutUserValidation(threadExecutor, postExecutionThread, LogoutUserValidator())
    val registrationValidation = RegisterUserValidation(threadExecutor, postExecutionThread, RegisterUserValidator())
    val loginValidation = LoginUserValidation(threadExecutor, postExecutionThread, LoginUserValidator())
    val deleteUserValidation = DeleteUserValidation(threadExecutor, postExecutionThread, DeleteUserValidator())
    val checkTokenValidation = CheckTokenValidation(threadExecutor, postExecutionThread, CheckTokenValidator.apply)

    ValidationContainer(logoutUserValidation, registrationValidation, loginValidation,
      deleteUserValidation, checkTokenValidation)
  }

  case class UseCaseContainer(loginUserUseCase: LoginUserUseCase,
                              logoutUserUseCase: LogoutUserUseCase,
                              registerUserUseCase: RegisterUserUseCase,
                              checkTokenUseCase: CheckTokenUseCase,
                              deleteUserUseCase: DeleteUserUseCase)

  case class ValidationContainer(logoutUserValidation: LogoutUserValidation,
                                 registerUserValidation: RegisterUserValidation,
                                 loginUserValidation: LoginUserValidation,
                                 deleteUserValidation: DeleteUserValidation,
                                 checkTokenValidation: CheckTokenValidation)

}
