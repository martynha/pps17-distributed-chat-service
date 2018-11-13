package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.service.webapp.interaction.Requests.GetUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.GetUserResult
import it.unibo.dcs.service.webapp.repositories.UserRepository
import rx.lang.scala.Observable

/**
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param userRepository      user repository reference
  * @usecase registration of a new user */
final class UserOfflineUseCase(private[this] val threadExecutor: ThreadExecutor,
                                private[this] val postExecutionThread: PostExecutionThread,
                                private[this] val userRepository: UserRepository)
  extends UseCase[GetUserResult, GetUserRequest](threadExecutor, postExecutionThread) with Logging {

  override protected[this] def createObservable(request: GetUserRequest): Observable[GetUserResult] =
    userRepository.getUserByUsername(request.username)
      .map(GetUserResult)

}

object UserOfflineUseCase {

  def apply(userRepository: UserRepository)(implicit ctx: Context): UserOfflineUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new UserOfflineUseCase(threadExecutor, postExecutionThread, userRepository)
  }

}
