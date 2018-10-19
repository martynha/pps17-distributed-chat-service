package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.RegisterResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository, UserRepository}
import rx.lang.scala.Observable

/** It represents the user registration functionality.
  * It calls the authentication service to retrieve the token,
  * then it contacts the User Service to store the new User and
  * finally it tells Room Service to .
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param userRepository      user repository reference
  * @param roomRepository      room repository reference
  * @usecase registration of a new user */
final class RegisterUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                                private[this] val postExecutionThread: PostExecutionThread,
                                private[this] val authRepository: AuthenticationRepository,
                                private[this] val userRepository: UserRepository,
                                private[this] val roomRepository: RoomRepository)
  extends UseCase[RegisterResult, RegisterUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(registerRequest: RegisterUserRequest): Observable[RegisterResult] = {
    for {
      token <- authRepository.registerUser(registerRequest)
      user <- userRepository.registerUser(registerRequest)
    } yield {
      roomRepository.registerUser(registerRequest).subscribe()
      RegisterResult(user, token)
    }
  }
}

/** Companion object */
object RegisterUserUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param userRepository user repository reference
    * @param roomRepository room repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def create(authRepository: AuthenticationRepository,
             userRepository: UserRepository,
             roomRepository: RoomRepository)(implicit ctx: Context): RegisterUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository, roomRepository)
  }
}
