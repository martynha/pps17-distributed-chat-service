package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.CreateRoomValidation
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.{CreateRoomRequest, JoinRoomRequest}
import rx.lang.scala.Observable

final class CreateRoomUseCase(threadExecutor: ThreadExecutor,
                              postExecutionThread: PostExecutionThread,
                              private[this] val roomRepository: RoomRepository,
                              private[this] val createRoomValidation: CreateRoomValidation)
  extends UseCase[Room, CreateRoomRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateRoomRequest): Observable[Room] =
    createRoomValidation(request)
      .flatMap(_ => roomRepository.createRoom(request))
      .flatMap(_ => roomRepository.joinRoom(JoinRoomRequest(request.name, request.username)))
      .map(_ => Room(request.name))

}
