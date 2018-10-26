package it.unibo.dcs.service.room.data.impl

import io.vertx.core.json.JsonArray
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.dataaccess.{DataStoreDatabase, ResultSetHelper}
import it.unibo.dcs.exceptions.{ParticipationNotFoundException, RoomNotFoundException}
import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase.Implicits._
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase.{deleteRoomQuery, insertParticipationQuery, insertRoomQuery, insertUserQuery, selectParticipationByKey, selectRoomByName}
import it.unibo.dcs.service.room.model._
import it.unibo.dcs.service.room.request._
import rx.lang.scala.Observable

import it.unibo.dcs.commons.dataaccess.Implicits.stringToDate

final class RoomDataStoreDatabase(connection: SQLConnection) extends DataStoreDatabase(connection) with RoomDataStore {

  override def createUser(request: CreateUserRequest): Observable[Unit] = execute(insertUserQuery, request)

  override def deleteRoom(request: DeleteRoomRequest): Observable[String] = execute(deleteRoomQuery, request)
    .map(_ => request.name)

  override def createRoom(request: CreateRoomRequest): Observable[Room] = execute(insertRoomQuery, request)
    .flatMap(_ => getRoomByName(GetRoomRequest(request.name)))

  override def getRoomByName(request: GetRoomRequest): Observable[Room] =
    query(selectRoomByName, request)
      .map { resultSet =>
        if (resultSet.getResults.isEmpty) {
          throw RoomNotFoundException(request.name)
        } else {
          ResultSetHelper.getRows(resultSet).head
        }
      }

  override def joinRoom(request: JoinRoomRequest): Observable[Participation] = execute(insertParticipationQuery, request)
    .flatMap(_ => getParticipationByKey(request))

  override def getParticipationByKey(request: JoinRoomRequest): Observable[Participation] =
    query(selectParticipationByKey, request)
      .map { resultSet =>
        if (resultSet.getResults.isEmpty) {
          throw ParticipationNotFoundException(request.username, request.name)
        } else {
          ResultSetHelper.getRows(resultSet).head
        }
      }
}

private[impl] object RoomDataStoreDatabase {

  val insertUserQuery = "INSERT INTO `users` (`username`) VALUES (?);"

  val insertRoomQuery = "INSERT INTO `rooms` (`name`,`owner_username`) VALUES (?, ?);"

  val insertParticipationQuery = "INSERT INTO `participations` (`username`, `name`) VALUES (?, ?)"

  val deleteRoomQuery = "DELETE FROM `rooms` WHERE `name` = ? AND `owner_username` = ?;"

  val selectRoomByName = "SELECT * FROM `rooms` WHERE `name` = ? "

  val selectParticipationByKey = "SELECT * FROM `participations` WHERE `username` = ? AND `name` = ?"

  object Implicits {

    implicit def requestToParams(request: CreateUserRequest): JsonArray = {
      new JsonArray().add(request.username)
    }

    implicit def requestToParams(request: CreateRoomRequest): JsonArray = {
      new JsonArray().add(request.name).add(request.username)
    }

    implicit def requestToParams(request: GetRoomRequest): JsonArray = {
      new JsonArray().add(request.name)
    }

    implicit def requestToParams(request: DeleteRoomRequest): JsonArray = {
      new JsonArray().add(request.name).add(request.username)
    }

    implicit def requestToParams(request: JoinRoomRequest): JsonArray = {
      new JsonArray().add(request.username).add(request.name)
    }

    implicit def jsonObjectToRoom(roomJsonObject: JsonObject): Room = {
      Room(roomJsonObject.getString("name"))
    }

    implicit def jsonObjectToParticipation(participationJsonObject: JsonObject): Participation = {
      Participation(Room(participationJsonObject.getString("name")),
        participationJsonObject.getString("username"),
        participationJsonObject.getString("join_date"))
    }
  }

}
