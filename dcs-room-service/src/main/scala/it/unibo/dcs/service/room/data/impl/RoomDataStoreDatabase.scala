package it.unibo.dcs.service.room.data.impl

import io.vertx.core.json.JsonArray
import io.vertx.scala.ext.sql.{SQLConnection, UpdateResult}
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.VertxHelper.Implicits.functionToHandler
import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase.Implicits._
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase.{deleteRoomQuery, insertUserQuery}
import it.unibo.dcs.service.room.request.{CreateUserRequest, DeleteRoomRequest}
import rx.lang.scala.Observable

final class RoomDataStoreDatabase(private[this] val connection: SQLConnection) extends RoomDataStore {

  override def createUser(request: CreateUserRequest): Observable[Unit] =
    VertxHelper.toObservable[UpdateResult] {
      connection.updateWithParams(insertUserQuery, request, _)
    }
      .map(_ => ())

  override def deleteRoom(request: DeleteRoomRequest): Observable[Unit] =
    VertxHelper.toObservable[UpdateResult] {
      connection.updateWithParams(deleteRoomQuery, request, _)
    }
      .map(_ => ())

}

object RoomDataStoreDatabase {

  private[impl] val insertUserQuery = "INSERT INTO `users` (`username`) VALUES (?);"

  private[impl] val deleteRoomQuery = "DELETE FROM `rooms` WHERE `name` = ? AND `owner_username` = ?;"

  object Implicits {

    implicit def requestToParams(request: CreateUserRequest): JsonArray = {
      new JsonArray().add(request.username)
    }

    implicit def requestToParams(request: DeleteRoomRequest): JsonArray = {
      new JsonArray().add(request.name).add(request.username)
    }

  }

}
