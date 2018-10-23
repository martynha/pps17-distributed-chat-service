package it.unibo.dcs.service.user.data.impl

import io.vertx.lang.scala.json.{JsonArray, JsonObject}
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.dataaccess.Implicits._
import it.unibo.dcs.commons.dataaccess.{DataStoreDatabase, ResultSetHelper}
import it.unibo.dcs.exceptions.{UserAlreadyExistsException, UserNotFoundException}
import it.unibo.dcs.service.user.data.UserDataStore
import it.unibo.dcs.service.user.data.impl.UserDataStoreDatabase.Implicits._
import it.unibo.dcs.service.user.data.impl.UserDataStoreDatabase._
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import rx.lang.scala.Observable

final class UserDataStoreDatabase(connection: SQLConnection) extends DataStoreDatabase(connection) with UserDataStore {

  override def checkIfUserExists(request: GetUserRequest): Observable[Unit] =
    query(selectUserByUsername, request)
    .map {resultSet =>
      if (resultSet.getResults.nonEmpty) {
        throw UserAlreadyExistsException(request.username)
      }
    }

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

  val insertUser = "INSERT INTO `users` (`username`, `first_name`, `last_name`) VALUES (?, ?, ?)"

  object Implicits {

    implicit def requestToParams(request: CreateUserRequest): JsonArray =
      new JsonArray().add(request.username).add(request.firstName).add(request.lastName)

    implicit def requestToParams(request: GetUserRequest): JsonArray =
      new JsonArray().add(request.username)

    implicit def jsonObjectToUser(userJsonObject: JsonObject): User = {
      User(userJsonObject.getString("username"),
        userJsonObject.getString("first_name"),
        userJsonObject.getString("last_name"),
        userJsonObject.getString("bio"),
        userJsonObject.getString("visible"),
        userJsonObject.getString("last_seen"))
    }

  }

}
