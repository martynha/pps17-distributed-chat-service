package it.unibo.dcs.service.authentication.interactor.usecases

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.authentication.interactor.usecases.helpers.ValidationHandler.validateAndContinue
import it.unibo.dcs.service.authentication.interactor.validations.LoginUserValidation
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import it.unibo.dcs.service.authentication.request.Requests.LoginUserRequest
import rx.lang.scala.Observable

/** It represents the use case to use to login as a user.
  * It checks in the database that the username and password are correct, through authRepository.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param jwtAuth             jwt authentication provider
  * @usecase login of a user */
final class LoginUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                             private[this] val postExecutionThread: PostExecutionThread,
                             private[this] val authRepository: AuthenticationRepository,
                             private[this] val jwtAuth: JWTAuth,
                             private[this] val loginUserValidation: LoginUserValidation)
  extends UseCase[String, LoginUserRequest](threadExecutor, postExecutionThread) with ReturningTokenUseCase {

  override protected[this] def createObservable(request: LoginUserRequest): Observable[String] =
    validateAndContinue(loginUserValidation, request,
      _ => authRepository.checkUserCredentials(request.username, request.password))
      .map(_ => createToken(request.username, jwtAuth))

}

object LoginUserUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param authRepository      authentication repository reference
    * @param jwtAuth             jwt authentication provider
    * @return the use case object */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            authRepository: AuthenticationRepository, jwtAuth: JWTAuth,
            loginUserValidation: LoginUserValidation): LoginUserUseCase =
    new LoginUserUseCase(threadExecutor, postExecutionThread, authRepository, jwtAuth, loginUserValidation)
}