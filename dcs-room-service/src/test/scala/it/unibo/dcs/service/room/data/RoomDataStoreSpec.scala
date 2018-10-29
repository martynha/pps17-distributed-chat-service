package it.unibo.dcs.service.room.data

import io.vertx.core.{AsyncResult, Handler}
import io.vertx.ext.unit.report.ReportOptions
import io.vertx.ext.unit.{Async, TestContext, TestOptions, TestSuite}
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.jdbc.JDBCClient
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.IoHelper
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest, GetRoomsRequest}

object RoomDataStoreSpec extends App {

  private var vertx: Vertx = _

  private var client: JDBCClient = _
  private var connection: SQLConnection = _

  private var roomDataStore: RoomDataStore = _

  TestSuite.create("RoomDataStoreSpec")
    .before(context => {
      vertx = Vertx.vertx
      vertx.exceptionHandler(context.exceptionHandler())

      val config = IoHelper.readJsonObject("/test_db_config.json")
      client = JDBCClient.createNonShared(vertx, config)

      val async = context.async(1)
      client.getConnection(context.asyncAssertSuccess(result => {
        connection = result
        roomDataStore = new RoomDataStoreDatabase(connection)
        async.countDown()
      }))
      async.await()
    })
    .after(context => {
      dropTables(connection, context)

      val closeAsync = context.async(3)
      connection.close(resultHandler(context, closeAsync))
      client.close(resultHandler(context, closeAsync))
      vertx.close(resultHandler(context, closeAsync))
      closeAsync.await()
    })
    .beforeEach(context => {
      dropTables(connection, context)

      createTables(connection, context)
    })
    .test("Create a user", context => {
      val insertAsync = context.async(1)
      roomDataStore.createUser(CreateUserRequest("mvandi"))
        .subscribe(_ => {
          connection.query("SELECT COUNT(*) FROM users", ar => {
            if (ar.succeeded) {
              val count = ar.result.getResults.head.getInteger(0)
              context.assertEquals(1, count)
              insertAsync.countDown()
            } else {
              context.fail(ar.cause())
            }
          })
        }, context.fail)
      insertAsync.await()
    })
    .test("Get all the rooms", testGetRoomsMethod _)
    .test("Delete a room", context => {
      val insertAsync = context.async(2)
      connection.execute("INSERT INTO `users` (`username`) VALUES ('mvandi')", context.asyncAssertSuccess(result => {
        insertAsync.countDown()
      }))

      connection.execute("INSERT INTO `rooms` (`name`, `owner_username`) VALUES ('Test room', 'mvandi')", context.asyncAssertSuccess(result => {
        insertAsync.countDown()
      }))
      insertAsync.await()

      val verifyInsertionAsync = context.async(1)
      connection.query("SELECT COUNT(*) FROM rooms", context.asyncAssertSuccess(result => {
        val count = result.getResults.head.getInteger(0)
        context.assertEquals(1, count)
        verifyInsertionAsync.countDown()
      }))
      verifyInsertionAsync.await()

      val deleteAsync = context.async(1)
      roomDataStore.deleteRoom(DeleteRoomRequest("Test room", "mvandi"))
        .subscribe(_ => {
          connection.query("SELECT COUNT(*) FROM rooms", ar => {
            if (ar.succeeded) {
              val count = ar.result.getResults.head.getInteger(0)
              context.assertEquals(0, count)
              deleteAsync.countDown()
            } else {
              context.fail(ar.cause())
            }
          })
        }, context.fail)
      deleteAsync.await()
    })
    .run(new TestOptions().addReporter(new ReportOptions().setTo("console")))

  private def testGetRoomsMethod(context: TestContext): Unit = {
    val exampleRoom = "TestExampleRoom1"
    val exampleUser = "TestExampleUser1"
    val selectAsync = context.async(1)
    roomDataStore.createUser(CreateUserRequest(exampleUser))
      .subscribe(_ => roomDataStore.createRoom(CreateRoomRequest(exampleRoom, exampleUser))
        .subscribe(_ => roomDataStore.getRooms(GetRoomsRequest())
          .subscribe(result => {
            assert(result.contains(Room(exampleRoom)))
            selectAsync.countDown()
          }, context.fail)))
    selectAsync.await()
  }

  private def resultHandler(context: TestContext, async: Async): Handler[AsyncResult[Unit]] = ar => {
    if (ar.succeeded()) {
      async.countDown()
    } else {
      context.fail(ar.cause())
    }
  }

  private def dropTables(connection: SQLConnection, context: TestContext): Unit =
    executeQueries(connection, context,
      "drop table if exists messages",
      "drop table if exists participations",
      "drop table if exists rooms",
      "drop table if exists users"
    )

  private def createTables(connection: SQLConnection, context: TestContext) =
    executeQueries(connection, context,
      "create table messages (" +
        "username varchar(20) not null," +
        "`name` varchar(50) not null," +
        "`timestamp` timestamp not null," +
        "content varchar(1024) not null," +
        "constraint id_message primary key (username, `name`, `timestamp`)," +
        "constraint FKPM foreign key (username, `name`) references participations (username, `name`) on delete cascade)",
      "create table participations (" +
        "username varchar(20) not null," +
        "`name` varchar(50) not null," +
        "join_date date not null," +
        "constraint id_participation primary key (username, `name`)," +
        "constraint FKPR foreign key (`name`) references rooms (`name`) on delete cascade," +
        "constraint FKUP foreign key (username) references users (username) on delete cascade)",
      "create table rooms (" +
        "`name` varchar(50) not null," +
        "owner_username varchar(20) not null," +
        "constraint id_room primary key (`name`)," +
        "constraint FKOR foreign key (owner_username) references users (username) on delete cascade)",
      "create table users (username varchar(20) not null," +
        "constraint id_user primary key (username))"
    )

  private def executeQueries(connection: SQLConnection, context: TestContext, queries: String*) = {
    val async = context.async(queries.size)
    queries.foreach(connection.execute(_, resultHandler(context, async)))
    async.await()
  }

}
