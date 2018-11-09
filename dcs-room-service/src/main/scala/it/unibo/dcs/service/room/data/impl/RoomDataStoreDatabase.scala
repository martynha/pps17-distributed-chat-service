package it.unibo.dcs.service.room.data.impl

import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson
import it.unibo.dcs.commons.dataaccess.Implicits.stringToDate
import it.unibo.dcs.commons.dataaccess.{DataStoreDatabase, ResultSetHelper}
import it.unibo.dcs.exceptions.{ParticipationNotFoundException, ParticipationsNotFoundException, RoomNotFoundException}
import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase.Implicits._
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase._
import it.unibo.dcs.service.room.gson
import it.unibo.dcs.service.room.model._
import it.unibo.dcs.service.room.request._
import rx.lang.scala.Observable

import it.unibo.dcs.commons.dataaccess.Implicits.{dateToString}

import scala.language.implicitConversions

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

  override def getRooms(request: GetRoomsRequest): Observable[List[Room]] =
    query(selectAllRooms, request)
    .map { resultSet =>
      ResultSetHelper.getRows(resultSet).map(jsonObjectToRoom).toList
    }

  override def joinRoom(request: JoinRoomRequest): Observable[Participation] =
    execute(insertParticipationQuery, request).flatMap(_ => getParticipationByKey(request))

  override def leaveRoom(request: LeaveRoomRequest): Observable[Participation] =
    getRoomParticipation(request)
      .flatMap(participation => execute(removeParticipationQuery, request)
        .map(_ => participation))

  override def getParticipationByKey(request: JoinRoomRequest): Observable[Participation] =
    getRoomParticipation(request)

  private def getRoomParticipation(request: JoinOrLeaveRoomRequest): Observable[Participation] =
    query(selectParticipationByKey, request)
      .map { resultSet =>
        if (resultSet.getResults.isEmpty) {
          throw ParticipationNotFoundException(request.username, request.name)
        } else {
          println(ResultSetHelper.getRows(resultSet).head.encodePrettily())
          ResultSetHelper.getRows(resultSet).head
        }
      }

  override def sendMessage(request: SendMessageRequest): Observable[Message] = execute(insertMessageQuery, request)
    .flatMap(_ => Observable.just(request))
    
  override def getRoomParticipations(request: GetRoomParticipationsRequest): Observable[Set[Participation]] = {
    query(selectParticipationsByRoomName, request)
      .map { resultSet =>
        if (resultSet.getResults.isEmpty) {
          throw ParticipationsNotFoundException(request.name)
        } else {
          // Debug
          ResultSetHelper.getRows(resultSet).foreach(row => println(row.encodePrettily()))

          ResultSetHelper.getRows(resultSet)
            .map(json => jsonObjectToParticipation(json))
            .toSet
        }
      }
  }
  
  override def getParticipationsByUsername(request: GetUserParticipationsRequest): Observable[List[Room]] =
    query(selectParticipationsByUsername, request)
      .map { resultSet =>
        if (resultSet.getResults.isEmpty) {
          List()
        } else {
          ResultSetHelper.getRows(resultSet).map(jsonObjectToRoom).toList
        }
      }

  override def getMessages(request: GetMessagesRequest): Observable[List[Message]] =
    query(selectMessagesByRoomName, request)
    .map { resultSet =>
      if (resultSet.getResults.isEmpty) {
        List()
      } else {
        ResultSetHelper.getRows(resultSet).foreach(row => println(row.encodePrettily()))
        ResultSetHelper.getRows(resultSet).map(jsonObjectToMessage).toList
      }
    }
}

private[impl] object RoomDataStoreDatabase {

  val insertUserQuery = "INSERT INTO `users` (`username`) VALUES (?);"

  val insertRoomQuery = "INSERT INTO `rooms` (`name`,`owner_username`) VALUES (?, ?);"

  val insertParticipationQuery = "INSERT INTO `participations` (`username`, `name`) VALUES (?, ?)"

  val insertMessageQuery = "INSERT INTO `messages` (`name`, `username`, `content`, `timestamp`) VALUES (?, ?, ?, ?)"
  
  val removeParticipationQuery = "DELETE FROM `participations` WHERE `username` = ? AND `name` = ? "

  val deleteRoomQuery = "DELETE FROM `rooms` WHERE `name` = ? AND `owner_username` = ?"

  val selectRoomByName = "SELECT `name` FROM `rooms` WHERE `name` = ? "

  val selectParticipationsByUsername = "SELECT `name` FROM `participations` WHERE `username` = ?"

  val selectAllRooms = s"SELECT `name` FROM `rooms` WHERE `name` NOT IN ($selectParticipationsByUsername)"

  val selectParticipationByKey = "SELECT * FROM `participations` WHERE `username` = ? AND `name` = ?"

  val selectParticipationsByRoomName = "SELECT * FROM `participations` WHERE `name` = ?"

  val selectMessagesByRoomName = "SELECT * FROM `messages` WHERE `name` = ?"

  object Implicits {

    implicit def requestToParams(request: CreateUserRequest): JsonArray =
      new JsonArray().add(request.username)

    implicit def requestToParams(request: CreateRoomRequest): JsonArray =
      new JsonArray().add(request.name).add(request.username)

    implicit def requestToParams(request: GetRoomRequest): JsonArray =
      new JsonArray().add(request.name)

    implicit def requestToParams(request: GetRoomsRequest): JsonArray =
      new JsonArray().add(request.username)

    implicit def requestToParams(request: DeleteRoomRequest): JsonArray =
      new JsonArray().add(request.name).add(request.username)

    /**
    * Useful for JoinRoomRequest and LeaveRoomRequest
    * */
    implicit def requestToParams(request: JoinOrLeaveRoomRequest): JsonArray =
      new JsonArray().add(request.username).add(request.name)

    implicit def requestToParams(request: SendMessageRequest): JsonArray = {
      val timestamp: String = request.timestamp
      new JsonArray().add(request.name).add(request.username)
        .add(request.content).add(timestamp)
    }
    
    implicit def requestToParams(request: GetRoomParticipationsRequest): JsonArray =
      Json.arr(request.name)

    implicit def requestToParams(request: GetUserParticipationsRequest): JsonArray =
      new JsonArray().add(request.username)

    implicit def requestToParams(request: GetMessagesRequest): JsonArray =
      new JsonArray().add(request.name)

    implicit def jsonObjectToRoom(json: JsonObject): Room = gson fromJsonObject[Room] json

    implicit def jsonObjectToParticipation(json: JsonObject): Participation = {
      val room = Room(json.getString("name"))
      val date = json.getString("join_date")
      val username = json.getString("username")
      Participation(room, username, date)
    }

    implicit def jsonObjectToMessage(json: JsonObject): Message = gson fromJsonObject[Message] json

    implicit def requestToMessage(request: SendMessageRequest): Message = Message(Room(request.name), request.username, request.content, request.timestamp)
  }

}