package it.unibo.dcs.service.user.repository.impl

import io.vertx.lang.scala.json.{JsonArray, JsonObject}
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.dataaccess.Implicits.{dateToString, _}
import it.unibo.dcs.commons.dataaccess.{DataStoreDatabase, ResultSetHelper}
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.model.exception.UserNotFoundException
import it.unibo.dcs.service.user.repository.UserDataStore
import it.unibo.dcs.service.user.model.Implicits._
import it.unibo.dcs.service.user.request.Implicits._
import it.unibo.dcs.service.user.repository.impl.UserDataStoreDatabase._
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import rx.lang.scala.Observable

final class UserDataStoreDatabase(connection: SQLConnection) extends DataStoreDatabase(connection) with UserDataStore {

  override def getUserByUsername(request: GetUserRequest): Observable[User] =
    query(selectUserByUsername, request)
      .map { resultSet =>
        if (resultSet.getResults.isEmpty) {
          throw UserNotFoundException(request.username)
        } else {
          ResultSetHelper.getRows(resultSet).head
        }
      }

  override def createUser(request: CreateUserRequest): Observable[User] =
    update(insertUser, request)
      .flatMap(_ => getUserByUsername(GetUserRequest(request.username)))

}

private[impl] object UserDataStoreDatabase {

  val selectUserByUsername = "SELECT * FROM `users` WHERE `username` = ?"

  val insertUser = "INSERT INTO `users` (`username`, `first_name`, `last_name`) VALUES (?, ?)"

}
