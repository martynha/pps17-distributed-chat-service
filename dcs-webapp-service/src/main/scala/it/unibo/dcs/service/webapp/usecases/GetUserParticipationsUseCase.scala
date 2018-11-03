package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, GetUserParticipationsRequest, LogoutUserRequest}
import it.unibo.dcs.service.webapp.interaction.Results.GetUserParticipationsResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository}
import rx.lang.scala.Observable

/** It represents the logout functionality.
  * It calls the authentication service to check the token validity and invalidate it.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @usecase user logout */
final class GetUserParticipationsUseCase(private[this] val threadExecutor: ThreadExecutor,
                                         private[this] val postExecutionThread: PostExecutionThread,
                                         private[this] val authRepository: AuthenticationRepository,
                                         private[this] val roomRepository: RoomRepository)
  extends UseCase[GetUserParticipationsResult, GetUserParticipationsRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetUserParticipationsRequest): Observable[GetUserParticipationsResult] = {
    authRepository.checkToken(CheckTokenRequest(request.token, request.username))
      .flatMap(_ => roomRepository.getUserParticipations(request))
      .map(GetUserParticipationsResult)
  }
}

object GetUserParticipationsUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param roomRepository room repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def apply(authRepository: AuthenticationRepository, roomRepository: RoomRepository)(implicit ctx: Context): GetUserParticipationsUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new GetUserParticipationsUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)
  }

}
