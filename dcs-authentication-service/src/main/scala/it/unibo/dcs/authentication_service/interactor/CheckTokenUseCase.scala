package it.unibo.dcs.authentication_service.interactor

import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.authentication_service.request.CheckTokenRequest
import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import rx.lang.scala.Observable

/** It represents the use case to use to check that the provided jwt token is valid.
  * It checks that the token is not present in the invalid_tokens table, through authRepository.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @usecase check the validity of the token */
final class CheckTokenUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val authRepository: AuthenticationRepository)
  extends UseCase[Boolean, CheckTokenRequest](threadExecutor, postExecutionThread) {

  override def createObservable(request: CheckTokenRequest): Observable[Boolean] =
    authRepository.isTokenValid(request.token)
}

object CheckTokenUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param authRepository      authentication repository reference
    * @return the use case object */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            authRepository: AuthenticationRepository) =
    new CheckTokenUseCase(threadExecutor, postExecutionThread, authRepository)
}