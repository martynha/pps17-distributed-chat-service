package it.unibo.dcs.service.webapp.repositories.datastores

import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi
import it.unibo.dcs.service.webapp.repositories.datastores.commons.DataStoreSpec
import it.unibo.dcs.service.webapp.repositories.datastores.impl.UserDataStoreNetwork
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class UserDataStoreSpec extends DataStoreSpec {

  private val userApi: UserApi = mock[UserApi]
  private val dataStore: UserDataStore = new UserDataStoreNetwork(userApi)

  private val createUserSubscriber = stub[Subscriber[User]]
  private val getUserSubscriber = stub[Subscriber[User]]
  private val deleteUserSubscriber = stub[Subscriber[String]]
  private val editUserSubscriber = stub[Subscriber[User]]

  it should "create a new user" in {
    // Given
    (userApi createUser _) expects registerRequest returns Observable.just(user) noMoreThanOnce()

    // When
    dataStore.createUser(registerRequest).subscribe(createUserSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (createUserSubscriber onNext _) verify user once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => createUserSubscriber onCompleted) verify() once()
  }

  it should "retrieve a user given its username" in {
    // Given
    (userApi getUserByUsername _) expects user.username returns Observable.just(user)

    // When
    dataStore.getUserByUsername(user.username).subscribe(getUserSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (getUserSubscriber onNext _) verify user once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => getUserSubscriber onCompleted) verify() once()
  }

  it should "delete an existing user" in {
    // Given
    (userApi createUser _) expects registerRequest returns Observable.just(user) noMoreThanOnce()
    (userApi deleteUser _) expects user.username returns Observable.just(user.username) once()

    // When
    dataStore.createUser(registerRequest)
      .flatMap(_ => dataStore.deleteUser(user.username))
      .subscribe(deleteUserSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (deleteUserSubscriber onNext _) verify user.username once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => deleteUserSubscriber onCompleted) verify() once()
  }

  it should "edit an existing user" in {
    // Given
    (userApi editUser _) expects editUserRequest returns Observable.just(user) once()

    // When
    dataStore.editUser(editUserRequest).subscribe(editUserSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (editUserSubscriber onNext _) verify user once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => editUserSubscriber onCompleted) verify() once()
  }
}
