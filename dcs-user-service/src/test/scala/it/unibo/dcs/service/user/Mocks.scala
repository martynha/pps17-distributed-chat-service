package it.unibo.dcs.service.user

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.user.data.UserDataStore
import it.unibo.dcs.service.user.repository.UserRepository
import org.scalamock.scalatest.MockFactory

object Mocks extends MockFactory {

  val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]

  val userRepository: UserRepository = mock[UserRepository]

  val userDataStore: UserDataStore = mock[UserDataStore]

}
