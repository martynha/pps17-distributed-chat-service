package it.unibo.dcs.service.webapp.usecases

import java.util.Date

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.AuthenticationRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}

abstract class UseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  protected val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  protected val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]

  protected val user = User("niklegend", "Nicola", "Piscaglia", "bio", visible = true, new Date)
  protected val token = "token"
  protected val authRepository: AuthenticationRepository = mock[AuthenticationRepository]

}
