package it.unibo.dcs.service.authentication.data

import java.util.Date

import io.vertx.scala.ext.sql.SQLConnection
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.Subscriber

import scala.language.postfixOps

class AuthenticationDataStoreTest extends FlatSpec with MockFactory {

  private val username = "ale"
  private val password = "123"
  private val token = "token"
  private val sqlConnection: SQLConnection = mock[SQLConnection]
  private val authDataStore: AuthenticationDataStore = new AuthenticationDataStoreDatabase(sqlConnection)

  it should "call sqlConnection and return a result when checkUserExistence is called" in {
    val loginSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyQueryExpectation()
    authDataStore checkUserExistence username subscribe loginSubscriber

    assertSubscriber(loginSubscriber)
  }

  it should "call sqlConnection and return a result when invalidToken is called" in {
    val invalidateTokenSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyUpdateExpectation()
    authDataStore invalidToken(token, new Date()) subscribe invalidateTokenSubscriber

    assertSubscriber(invalidateTokenSubscriber)
  }

  it should "call sqlConnection and return a result when createUser is called" in {
    val registerSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyUpdateExpectation()
    authDataStore createUser(username, password) subscribe registerSubscriber

    assertSubscriber(registerSubscriber)
  }

  it should "call sqlConnection and return a result when deleteUser is called" in {
    val deleteSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyUpdateExpectation()
    authDataStore deleteUser(username, password) subscribe deleteSubscriber

    assertSubscriber(deleteSubscriber)
  }

  it should "call sqlConnection and return a result when isTokenInvalid is called" in {
    val subscriber: Subscriber[Boolean] = stub[Subscriber[Boolean]]
    specifyQueryExpectation()
    authDataStore isTokenValid token subscribe subscriber

    assertSubscriber(subscriber)
  }

  private def specifyQueryExpectation(): Unit = {
    (sqlConnection query(_, _)) expects(*, *) returns sqlConnection
  }

  private def specifyUpdateExpectation(): Unit = {
    (sqlConnection updateWithParams(_, _, _)) expects(*, *, *) returns sqlConnection
  }

  private def assertSubscriber(subscriber: Subscriber[_]): Unit = {
    (() => subscriber onStart) verify() once()
  }
}