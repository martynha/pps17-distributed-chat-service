package it.unibo.dcs.service.webapp.usecases.commons

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository, UserRepository}
import org.scalamock.scalatest.MockFactory

/**
  * It encapsulates the mock objects used by the tests
  **/
private[usecases] object Mocks extends MockFactory {

  val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]

  val roomRepository: RoomRepository = mock[RoomRepository]
  val authRepository: AuthenticationRepository = mock[AuthenticationRepository]
  val userRepository: UserRepository = mock[UserRepository]
}
