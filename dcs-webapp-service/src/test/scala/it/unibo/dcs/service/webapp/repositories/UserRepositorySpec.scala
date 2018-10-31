package it.unibo.dcs.service.webapp.repositories

import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.UserRepository
import it.unibo.dcs.service.webapp.repositories.datastores.UserDataStore
import it.unibo.dcs.service.webapp.repositories.impl.UserRepositoryImpl
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class UserRepositorySpec extends RepositorySpec {

  private val userDataStore: UserDataStore = mock[UserDataStore]
  private val repository: UserRepository = new UserRepositoryImpl(userDataStore)

  private val createUserSubscriber: Subscriber[User] = stub[Subscriber[User]]
  private val getUserSubscriber: Subscriber[User] = stub[Subscriber[User]]
  private val deleteUserSubscriber: Subscriber[String] = stub[Subscriber[String]]

  it should "register a new user" in {
    // Given
    (userDataStore createUser _) expects registerRequest returns Observable.just(user) noMoreThanOnce()

    // When
    repository registerUser registerRequest subscribe createUserSubscriber

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (createUserSubscriber onNext _) verify user once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => createUserSubscriber onCompleted) verify() once()
  }

  it should "retrieve a registered user given its username" in {
    // Given
    (userDataStore getUserByUsername _) expects user.username returns Observable.just(user)

    // When
    repository.getUserByUsername(user.username).subscribe(getUserSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `user` as argument
    (getUserSubscriber onNext _) verify user once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => getUserSubscriber onCompleted) verify() once()
  }

  it should "delete a registered user given its information" in {
    // Given
    (userDataStore deleteUser _) expects user.username returns Observable.just(user.username) noMoreThanOnce()

    // When
    repository.deleteUser(user.username).subscribe(deleteUserSubscriber)

    // Then
    (deleteUserSubscriber onNext _) verify user.username once()
    (() => deleteUserSubscriber onCompleted) verify() once()
  }
}
