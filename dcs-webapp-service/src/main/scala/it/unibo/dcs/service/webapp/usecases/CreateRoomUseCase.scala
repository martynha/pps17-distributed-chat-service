package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.CreateRoomRequest
import it.unibo.dcs.service.webapp.interaction.Results.RoomCreationResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository}
import rx.lang.scala.Observable

/** It represents the room creation functionality.
  * It calls the authentication service to check the token validity and then
  * it contacts the room service to store the new room. */
final class CreateRoomUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val authRepository: AuthenticationRepository,
                              private[this] val roomRepository: RoomRepository)
  extends UseCase[RoomCreationResult, CreateRoomRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateRoomRequest): Observable[RoomCreationResult] =
    for {
      _ <- authRepository.createRoom(request)
      room <- roomRepository.createRoom(request)
    } yield RoomCreationResult(room)
}

object CreateRoomUseCase {
  def create(authRepository: AuthenticationRepository, roomRepository: RoomRepository)
            (implicit ctx: Context): CreateRoomUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new CreateRoomUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)
  }
}