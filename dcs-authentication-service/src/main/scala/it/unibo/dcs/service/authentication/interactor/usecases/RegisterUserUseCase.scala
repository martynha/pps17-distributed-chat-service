package it.unibo.dcs.service.authentication.interactor.usecases

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.authentication.interactor.usecases.helpers.ValidationHandler.validateAndContinue
import it.unibo.dcs.service.authentication.interactor.validations.RegisterUserValidation
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import it.unibo.dcs.service.authentication.request.Requests.RegisterUserRequest
import rx.lang.scala.Observable

/** It represents the use case to use to register a user.
  * It saves the new user to the database, through authRepository.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param jwtAuth             jwt authentication provider
  * @usecase registration of a user */
final class RegisterUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                                private[this] val postExecutionThread: PostExecutionThread,
                                private[this] val authRepository: AuthenticationRepository,
                                private[this] val jwtAuth: JWTAuth,
                                private[this] val registerUserValidation: RegisterUserValidation)
  extends UseCase[String, RegisterUserRequest](threadExecutor, postExecutionThread) with ReturningTokenUseCase {

  override protected[this] def createObservable(request: RegisterUserRequest): Observable[String] =
    validateAndContinue(registerUserValidation, request,
      _ => authRepository.createUser(request.username, request.password))
      .map(_ => createToken(request.username, jwtAuth))
}

object RegisterUserUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param authRepository      authentication repository reference
    * @param jwtAuth             jwt authentication provider
    * @return the use case object */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            authRepository: AuthenticationRepository, jwtAuth: JWTAuth,
            registerUserValidation: RegisterUserValidation): RegisterUserUseCase =
    new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository, jwtAuth, registerUserValidation)
}