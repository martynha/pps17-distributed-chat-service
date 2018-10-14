package it.unibo.dcs.authentication_service.data

import java.util.Date

import io.vertx.core.Future
import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.authentication_service.data.AuthenticationDataStoreDatabase.{insertInvalidToken, insertUser}
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.dataaccess.DataStoreDatabase
import it.unibo.dcs.commons.dataaccess.Implicits.dateToString
import rx.lang.scala.Observable

class AuthenticationDataStoreDatabase(private[this] val connection: SQLConnection)
  extends DataStoreDatabase(connection) with AuthenticationDataStore {

  override def createUser(username: String, password: String): Observable[Unit] = {
    execute(insertUser, Json.arr(username, password))
  }

  override def checkUserExistence(username: String, password: String): Observable[Unit] =
    checkRecordPresence("users", ("username", username), ("password", password))

  override def invalidToken(token: String, expirationDate: Date): Observable[Unit] =
    execute(insertInvalidToken, Json.arr(token, dateToString(expirationDate)))

  override def isTokenInvalid(token: String): Observable[Boolean] =
    checkAtLeastOneRecord("SELECT * FROM invalid_tokens WHERE token = " + token)

  private def checkRecordPresence(table: String, parameters: (String, String)*): Observable[Unit] = {
    VertxHelper.toObservable[Unit] { handler =>
      val query = "SELECT * FROM " + table + " WHERE " +
        parameters.map(param => param._1 + " = " + "'" + param._2 + "'")
        .fold("")((param1, param2) => param1 + " AND " + param2)
      val completeQuery = query.replaceFirst("AND", "")
      connection.query(completeQuery, ar => {
        val result: Future[Unit] = Future.future()
          if (ar.succeeded() && ar.result().getResults.nonEmpty) {
            result.complete()
          } else {
            result.fail(ar.cause())
          }
        handler(result)
      })
    }
  }

  private def checkAtLeastOneRecord(query: String): Observable[Boolean] = {
    VertxHelper.toObservable[Boolean] { handler =>
      connection.query(query, ar => {
        val result: Future[Boolean] = Future.future()
        if (ar.succeeded()) {
          result.complete(ar.result().getResults.nonEmpty)
        } else {
          result.fail(ar.cause())
        }
        handler(result)
      })
    }
  }
}

private[data] object AuthenticationDataStoreDatabase {

  val insertUser = "INSERT INTO `users` (`username`, `password`) VALUES (?, ?);"

  val insertInvalidToken = "INSERT INTO `invalid_tokens` (`token`, `expiration_date`) VALUES (?, ?);"

}
