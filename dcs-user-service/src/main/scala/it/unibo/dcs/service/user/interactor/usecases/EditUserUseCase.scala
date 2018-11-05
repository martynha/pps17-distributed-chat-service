package it.unibo.dcs.service.user.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.user.interactor.validations.ValidateUserEditing
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.EditUserRequest
import rx.lang.scala.Observable

final class EditUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val userRepository: UserRepository,
                              private[this] val validation: ValidateUserEditing)
  extends UseCase[User, EditUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: EditUserRequest): Observable[User] =
    validation(request).flatMap(_ => userRepository.editUser(request))

}
