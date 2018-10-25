package it.unibo.dcs.service.authentication.interactor.usecases

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
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
                             private[this] val jwtAuth: JWTAuth)
  extends ReturningTokenUseCase(threadExecutor, postExecutionThread, authRepository, jwtAuth) {

  protected def getMainObservable(username: String, password: String): Observable[Unit] = {
    authRepository.checkUserCredentials(username, password)
  }

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
            authRepository: AuthenticationRepository, jwtAuth: JWTAuth): LoginUserUseCase =
    new LoginUserUseCase(threadExecutor, postExecutionThread, authRepository, jwtAuth)
}