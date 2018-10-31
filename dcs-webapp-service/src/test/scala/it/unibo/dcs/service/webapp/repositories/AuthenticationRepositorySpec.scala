package it.unibo.dcs.service.webapp.repositories

import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.repositories.AuthenticationRepository
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore
import it.unibo.dcs.service.webapp.repositories.impl.AuthenticationRepositoryImpl
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class AuthenticationRepositorySpec extends RepositorySpec {

  private val dataStore: AuthenticationDataStore = mock[AuthenticationDataStore]
  private val repository: AuthenticationRepository = new AuthenticationRepositoryImpl(dataStore)

  private val token = "token"

  private val roomCreationRequest = CheckTokenRequest(token)
  private val loginUserRequest = LoginUserRequest(user.username, "password")
  private val logoutUserRequest = LogoutUserRequest(user.username, token)
  private val deleteUserRequest = DeleteUserRequest(user.username, token)

  private val registeredSubscriber: Subscriber[String] = stub[Subscriber[String]]
  private val roomCreationSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  private val loginSubscriber: Subscriber[String] = stub[Subscriber[String]]
  private val logoutSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  private val deleteUserSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]

  it should "create a new room" in {
    // Given
    (dataStore checkToken _) expects roomCreationRequest returns (Observable just token) once()

    // When
    repository.checkToken(roomCreationRequest).subscribe(roomCreationSubscriber)

    // Then
    // Verify that `subscriber.onCompleted` has been called once
    (() => roomCreationSubscriber onCompleted) verify() once()
  }

  it should "register a new user" in {
    // Given
    (dataStore registerUser _) expects registerRequest returns (Observable just token) noMoreThanOnce()

    // When
    repository.registerUser(registerRequest).subscribe(registeredSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (registeredSubscriber onNext _) verify token once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => registeredSubscriber onCompleted) verify() once()
  }


  it should "authenticate a registered user" in {
    // Given
    (dataStore registerUser _) expects registerRequest returns (Observable just token)
    (dataStore loginUser _) expects loginUserRequest returns (Observable just token)

    // When
    repository.registerUser(registerRequest).subscribe(_ => {
      repository.loginUser(loginUserRequest).subscribe(loginSubscriber)
    })

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (loginSubscriber onNext _) verify token once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => loginSubscriber onCompleted) verify() once()
  }


  it should "logout a logged user" in {
    // Given
    (dataStore loginUser _) expects loginUserRequest returns (Observable just token)
    (dataStore logoutUser _) expects logoutUserRequest returns Observable.empty

    // When
    repository.loginUser(loginUserRequest).subscribe(_ => {
      repository.logoutUser(logoutUserRequest).subscribe(logoutSubscriber)
    })

    // Then
    // Verify that `subscriber.onCompleted` has been called once
    (() => logoutSubscriber onCompleted) verify() once()
  }

  it should "delete a previously saved user" in {
    // Given
    (dataStore registerUser _) expects registerRequest returns (Observable just token)
    (dataStore deleteUser _) expects deleteUserRequest returns Observable.empty

    // When
    repository.registerUser(registerRequest)
      .flatMap(_ => repository.deleteUser(deleteUserRequest))
      .subscribe(deleteUserSubscriber)

    // Then
    (() => deleteUserSubscriber onCompleted) verify() once()
  }
}
