package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository}

/** It represents the room join functionality.
  * It calls the authentication service to check the token validity and then
  * it contacts the room service to join the user to the room.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param roomRepository      room repository reference
  * @usecase user join a room */
final class GetRoomsUseCase (private[this] val threadExecutor: ThreadExecutor,
                             private[this] val postExecutionThread: PostExecutionThread,
                             private[this] val authRepository: AuthenticationRepository,
                             private[this] val roomRepository: RoomRepository)
  extends UseCase[RoomJoinResult, RoomJoinRequest](threadExecutor, postExecutionThread) {

  import it.unibo.dcs.commons.interactor.UseCase
  import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
  import it.unibo.dcs.service.webapp.interaction.Requests.RoomJoinRequest
  import it.unibo.dcs.service.webapp.interaction.Results.RoomJoinResult
  import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository}

}
