package it.unibo.dcs.authentication_service.interactor

import java.util.Date

import it.unibo.dcs.authentication_service.business_logic.JwtTokenDecoder
import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.authentication_service.request.LogoutUserRequest
import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import rx.lang.scala.Observable

/** It represents the use case to use to logout a user.
  * It adds the provided jwt token to the set of invalid tokens, through authRepository.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @usecase logout of a user */
final class LogoutUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                         private[this] val postExecutionThread: PostExecutionThread,
                         private[this] val authRepository: AuthenticationRepository)
  extends UseCase[Unit, LogoutUserRequest](threadExecutor, postExecutionThread) {

  val tokenDecoder = JwtTokenDecoder()

  override protected[this] def createObservable(request: LogoutUserRequest): Observable[Unit] =
    authRepository.invalidToken(request.token, new Date())
}

object LogoutUserUseCase{

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param authRepository      authentication repository reference
    * @return the use case object */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            authRepository: AuthenticationRepository) =
    new LogoutUserUseCase(threadExecutor, postExecutionThread, authRepository)
}