package it.unibo.dcs.service.room.data.impl

import io.vertx.core.json.JsonArray
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.dataaccess.DataStoreDatabase
import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase.Implicits._
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase.{deleteRoomQuery, insertRoomQuery, insertUserQuery}
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest}
import rx.lang.scala.Observable

final class RoomDataStoreDatabase(connection: SQLConnection) extends DataStoreDatabase(connection) with RoomDataStore {

  override def createUser(request: CreateUserRequest): Observable[Unit] = execute(insertUserQuery, request)

  override def deleteRoom(request: DeleteRoomRequest): Observable[Unit] = execute(deleteRoomQuery, request)

  override def createRoom(request: CreateRoomRequest): Observable[Unit] = execute(insertRoomQuery, request)

}

private[impl] object RoomDataStoreDatabase {

  val insertUserQuery = "INSERT INTO `users` (`username`) VALUES (?);"

  val insertRoomQuery = "INSERT INTO `rooms` (`name`,`owner_username`) VALUES (?, ?);"

  val deleteRoomQuery = "DELETE FROM `rooms` WHERE `name` = ? AND `owner_username` = ?;"

  object Implicits {

    implicit def requestToParams(request: CreateUserRequest): JsonArray = {
      new JsonArray().add(request.username)
    }

    implicit def requestToParams(request: CreateRoomRequest): JsonArray = {
      new JsonArray().add(request.name).add(request.username)
    }

    implicit def requestToParams(request: DeleteRoomRequest): JsonArray = {
      new JsonArray().add(request.name).add(request.username)
    }

  }

}
