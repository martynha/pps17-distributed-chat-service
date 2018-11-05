package it.unibo.dcs.service.authentication.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.authentication.interactor.usecases.helpers.ValidationHandler.validateAndContinue
import it.unibo.dcs.service.authentication.interactor.validations.DeleteUserValidation
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import it.unibo.dcs.service.authentication.request.Requests.DeleteUserRequest
import rx.lang.scala.Observable

/** It represents the use case to use to delete a user.
  * It adds the provided jwt token to the set of invalid tokens, through authRepository.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @usecase deletion of a user */
final class DeleteUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val authRepository: AuthenticationRepository,
                              private[this] val deleteUserValidation: DeleteUserValidation)
  extends UseCase[Unit, DeleteUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: DeleteUserRequest): Observable[Unit] =
    validateAndContinue(deleteUserValidation, request, _ => authRepository.deleteUser(request.username, request.token))
}

object DeleteUserUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param authRepository      authentication repository reference
    * @return the use case object */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            authRepository: AuthenticationRepository, deleteUserValidation: DeleteUserValidation): DeleteUserUseCase =
    new DeleteUserUseCase(threadExecutor, postExecutionThread, authRepository, deleteUserValidation)
}