package it.unibo.dcs.authentication_service.register

import it.unibo.dcs.authentication_service.common.AuthenticationRepository
import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import rx.lang.scala.Observable

final class RegisterUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val authRepository: AuthenticationRepository)
  extends UseCase[String, RegisterUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: RegisterUserRequest): Observable[String] =
    authRepository.createUser(request.username, request.password)
}