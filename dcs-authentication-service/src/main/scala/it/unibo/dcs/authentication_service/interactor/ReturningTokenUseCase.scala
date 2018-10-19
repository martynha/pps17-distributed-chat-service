package it.unibo.dcs.authentication_service.interactor

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.authentication_service.business_logic.JwtTokenGenerator
import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.authentication_service.request.TokenRequest
import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import rx.lang.scala.Observable

/** It represents any use case that should return a jwt token
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param jwtAuth             jwt authentication provider
  * @usecase creation of a jwt token */
abstract class ReturningTokenUseCase(private[this] val threadExecutor: ThreadExecutor,
                             private[this] val postExecutionThread: PostExecutionThread,
                             private[this] val authRepository: AuthenticationRepository,
                             private[this] val jwtAuth: JWTAuth)
  extends UseCase[String, TokenRequest](threadExecutor, postExecutionThread) {

  val tokenGenerator = JwtTokenGenerator(jwtAuth)

  override protected[this] def createObservable(request: TokenRequest): Observable[String] =
    getMainObservable(request.username, request.password).map(_ => tokenGenerator.generateToken(request.username))

  /** Method to override to define the observable that the use case generates
    *
    * @param username the username of the user
    * @param password the password of the user */
  protected def getMainObservable(username: String, password: String): Observable[Unit]
}