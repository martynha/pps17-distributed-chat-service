package it.unibo.dcs.service.user.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.user.repository.UserRepository
import rx.lang.scala.Observable

final class UpdateUserAccessUseCase(private[this] val threadExecutor: ThreadExecutor,
                                    private[this] val postExecutionThread: PostExecutionThread,
                                    private[this] val userRepository: UserRepository)
  extends UseCase[Unit, String](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(username: String): Observable[Unit] =
    userRepository.updateAccess(username)

}
