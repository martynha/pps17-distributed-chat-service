package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, GetRoomParticipationsRequest}
import it.unibo.dcs.service.webapp.interaction.Results.GetRoomParticipationsResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository}
import rx.lang.scala.Observable

/** It represents the get room participations functionality.
  * It calls the authentication service to check the token validity and then
  * contact the room service to get all the participation for a given room name.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param roomRepository      room repository reference
  * @usecase get all the participations for a given room */
final class GetRoomParticipationsUseCase(private[this] val threadExecutor: ThreadExecutor,
                                         private[this] val postExecutionThread: PostExecutionThread,
                                         private[this] val authRepository: AuthenticationRepository,
                                         private[this] val roomRepository: RoomRepository)
  extends UseCase[GetRoomParticipationsResult, GetRoomParticipationsRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetRoomParticipationsRequest): Observable[GetRoomParticipationsResult] =
    for {
      _ <- authRepository checkToken CheckTokenRequest(request.token, request.username)
      rooms <- roomRepository getRoomParticipations request
    } yield GetRoomParticipationsResult(rooms)
}

object GetRoomParticipationsUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param roomRepository room repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def apply(authRepository: AuthenticationRepository, roomRepository: RoomRepository)
           (implicit ctx: Context): GetRoomParticipationsUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new GetRoomParticipationsUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)
  }
}
