package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.LogoutUserRequest
import it.unibo.dcs.service.webapp.repositories.AuthenticationRepository
import rx.lang.scala.Observable

/** It represents the logout functionality.
  * It calls the authentication service to check the token validity and invalidate it.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @usecase user logout */
final class LogoutUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val authRepository: AuthenticationRepository)
  extends UseCase[Unit, LogoutUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(logoutRequest: LogoutUserRequest): Observable[Unit] = {
    authRepository.logoutUser(logoutRequest)
  }
}

/** Companion object */
object LogoutUserUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def create(authRepository: AuthenticationRepository)(implicit ctx: Context): LogoutUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new LogoutUserUseCase(threadExecutor, postExecutionThread, authRepository)
  }
}
