package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.RegisterResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import rx.lang.scala.Observable

/** It represents the user registration functionality.
  * It calls the authentication service to retrieve the token,
  * then it contacts the User Service to store the new User. */
final class RegisterUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                                private[this] val postExecutionThread: PostExecutionThread,
                                private[this] val authRepository: AuthenticationRepository,
                                private[this] val userRepository: UserRepository)
  extends UseCase[RegisterResult, RegisterUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(registerRequest: RegisterUserRequest): Observable[RegisterResult] = {
    for {
      token <- authRepository.registerUser(registerRequest)
      user <- userRepository.registerUser(registerRequest)
    } yield RegisterResult(user, token)
  }
}

object RegisterUserUseCase {
  def create(authRepository: AuthenticationRepository,
             userRepository: UserRepository)(implicit ctx: Context): RegisterUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository)
  }
}
