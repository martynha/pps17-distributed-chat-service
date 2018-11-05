package it.unibo.dcs.service.webapp.repositories.datastores

import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.repositories.datastores.api.AuthenticationApi
import it.unibo.dcs.service.webapp.repositories.datastores.commons.DataStoreSpec
import it.unibo.dcs.service.webapp.repositories.datastores.impl.AuthenticationDataStoreNetwork
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class AuthenticationDataStoreSpec extends DataStoreSpec {

  private val authApi: AuthenticationApi = mock[AuthenticationApi]
  private val dataStore: AuthenticationDataStore = new AuthenticationDataStoreNetwork(authApi)

  private val loginUserRequest = LoginUserRequest(user.username, "password")
  private val logoutUserRequest = LogoutUserRequest(user.username, token)
  private val roomCreationRequest = CheckTokenRequest(token, user.username)
  private val deleteUserRequest = DeleteUserRequest(user.username, token)

  private val registeredSubscriber: Subscriber[String] = stub[Subscriber[String]]
  private val roomCreationSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  private val loginSubscriber: Subscriber[String] = stub[Subscriber[String]]
  private val logoutSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  private val deleteUserSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]

  it should "check the creation of a new room" in {
    // Given
    (authApi checkToken _) expects roomCreationRequest returns (Observable just token)

    // When
    dataStore.checkToken(roomCreationRequest).subscribe(roomCreationSubscriber)

    // Then
    // Verify that `subscriber.onCompleted` has been called once
    (() => roomCreationSubscriber onCompleted) verify() once()
  }

  it should "register a new user" in {
    // Given
    (authApi registerUser _) expects registerRequest returns (Observable just token)

    // When
    dataStore.registerUser(registerRequest).subscribe(registeredSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (registeredSubscriber onNext _) verify token once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => registeredSubscriber onCompleted) verify() once()
  }


  it should "authenticate a registered user" in {
    // Given
    (authApi registerUser _) expects registerRequest returns (Observable just token)
    (authApi loginUser _) expects loginUserRequest returns (Observable just token)

    // When
    dataStore.registerUser(registerRequest).subscribe(_ => {
      dataStore.loginUser(loginUserRequest).subscribe(loginSubscriber)
    })

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (loginSubscriber onNext _) verify token once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => loginSubscriber onCompleted) verify() once()
  }


  it should "logout a logged user" in {
    // Given
    (authApi loginUser _) expects loginUserRequest returns (Observable just token)
    (authApi logoutUser _) expects logoutUserRequest returns Observable.empty

    // When
    dataStore.loginUser(loginUserRequest).subscribe(_ => {
      dataStore.logoutUser(logoutUserRequest).subscribe(logoutSubscriber)
    })

    // Then
    // Verify that `subscriber.onCompleted` has been called once
    (() => logoutSubscriber onCompleted) verify() once()
  }

  it should "delete an existing user" in {
    // Given
    (authApi registerUser _) expects registerRequest returns (Observable just token)
    (authApi deleteUser _) expects deleteUserRequest returns Observable.empty

    // When
    dataStore.registerUser(registerRequest)
      .flatMap(_ => dataStore.deleteUser(deleteUserRequest))
      .subscribe(deleteUserSubscriber)

    // Then
    // Verify that `subscriber.onCompleted` has been called once
    (() => deleteUserSubscriber onCompleted) verify() once()
  }


}
