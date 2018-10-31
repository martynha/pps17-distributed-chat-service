package it.unibo.dcs.service.user.interactor.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.user.interactor.validations.ValidateUserCreation
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.CreateUserRequest
import rx.lang.scala.Observable

final class CreateUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val userRepository: UserRepository,
                              private[this] val validation: ValidateUserCreation)
  extends UseCase[User, CreateUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateUserRequest): Observable[User] =
    validation(request).flatMap(_ => userRepository.createUser(request))

}

/*
/** Companion object */
object CreateUserUseCase {

  /** Factory method to create the use case
    *
    * @param userRepository user repository reference
    * @param ctx            Vertx context
    * @return               the use case object */
  def create(userRepository: UserRepository)(implicit ctx: Context): CreateUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new CreateUserUseCase(threadExecutor, postExecutionThread, userRepository)
  }

}
*/