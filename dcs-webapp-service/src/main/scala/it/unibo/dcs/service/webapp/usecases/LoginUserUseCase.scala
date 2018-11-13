package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.LoginUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.LoginResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import rx.lang.scala.Observable

/** It represents the login functionality.
  * It calls the authentication service to check the credentials and retrieve the token,
  * then it contacts the User Service to retrieve the User information.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param userRepository      user repository reference
  * @usecase user login */
final class LoginUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                             private[this] val postExecutionThread: PostExecutionThread,
                             private[this] val authRepository: AuthenticationRepository,
                             private[this] val userRepository: UserRepository)
  extends UseCase[LoginResult, LoginUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(loginRequest: LoginUserRequest): Observable[LoginResult] = {
    for {
      token <- authRepository.loginUser(loginRequest)
      user <- userRepository.getUserByUsername(loginRequest.username)
      _ <- userRepository.updateAccess(user.username)
    } yield LoginResult(user, token)
  }

}

/** Companion object */
object LoginUserUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param userRepository user repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def create(authRepository: AuthenticationRepository,
             userRepository: UserRepository)(implicit ctx: Context): LoginUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new LoginUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository)
  }
}
