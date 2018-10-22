package repositories.datastores

import java.util.Date

import it.unibo.dcs.service.webapp.interaction.Requests._
import it.unibo.dcs.service.webapp.model.{Room, User}
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore
import it.unibo.dcs.service.webapp.repositories.datastores.api.AuthenticationApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.AuthenticationDataStoreNetwork
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class AuthenticationDataStoreSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  private val authApi: AuthenticationApi = mock[AuthenticationApi]
  private val dataStore: AuthenticationDataStore = new AuthenticationDataStoreNetwork(authApi)
  private val user = User("niklegend", "nicola", "piscaglia", "bla", visible = true, new Date())
  private val token = "token"
  private val registerRequest = RegisterUserRequest(user.username, user.firstName,
    user.lastName, "password", "password")
  private val loginUserRequest = LoginUserRequest(user.username, "password")
  private val logoutUserRequest = LogoutUserRequest(user.username, token)
  private val roomCreationRequest = CheckTokenRequest(token)
  private val registeredSubscriber: Subscriber[String] = stub[Subscriber[String]]
  private val roomCreationSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  private val loginSubscriber: Subscriber[String] = stub[Subscriber[String]]
  private val logoutSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]


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


}
