package it.unibo.dcs.service.room

import io.vertx.core.{AbstractVerticle, Context, Vertx => JVertx}
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.BodyHandler
import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson
import it.unibo.dcs.commons.VertxWebHelper.Implicits.contentTypeToString
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.PostExecutionThread
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, ServiceVerticle}
import it.unibo.dcs.commons.{RxHelper, VertxWebHelper}
import it.unibo.dcs.service.room.RoomVerticle.Implicits._
import it.unibo.dcs.service.room.interactor.usecases._
import it.unibo.dcs.service.room.interactor.validations._
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request._
import it.unibo.dcs.service.room.subscriber._
import it.unibo.dcs.service.room.validator._
import org.apache.http.entity.ContentType

import scala.language.implicitConversions

final class RoomVerticle(private[this] val roomRepository: RoomRepository, val publisher: HttpEndpointPublisher) extends ServiceVerticle {

  private[this] var host: String = _
  private[this] var port: Int = _

  override def init(jVertx: JVertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)

    host = config.getString("host")
    port = config.getInteger("port")
  }

  override protected def initializeRouter(router: Router): Unit = {
    router.route().handler(BodyHandler.create())
    VertxWebHelper.setupCors(router)

    val threadExecutor = ThreadExecutorExecutionContext(vertx)
    val postExecutionThread = PostExecutionThread(RxHelper.scheduler(this.ctx))

    val createUserUseCase = {
      val validation = new CreateUserValidation(threadExecutor, postExecutionThread, CreateUserValidator())
      new CreateUserUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    val getRoomsUseCase = {
      val validation = new GetRoomsValidation(threadExecutor, postExecutionThread, GetRoomsValidator())
      new GetRoomsUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    val createRoomUseCase = {
      val validation = new CreateRoomValidation(threadExecutor, postExecutionThread, CreateRoomValidator())
      new CreateRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    val deleteRoomUseCase = {
      val validation = new DeleteRoomValidation(threadExecutor, postExecutionThread, DeleteRoomValidator())
      new DeleteRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    val joinRoomUseCase = {
      val validation = new JoinRoomValidation(threadExecutor, postExecutionThread, JoinRoomValidator())
      new JoinRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }

    router.post("/createUser")
      .consumes(ContentType.APPLICATION_JSON)
      .consumes(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val subscriber = new CreateUserSubscriber(routingContext.response())
        createUserUseCase(request, subscriber)
      })

    router.post("/createRoom")
      .consumes(ContentType.APPLICATION_JSON)
      .consumes(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val subscriber = new CreateRoomSubscriber(routingContext.response())
        createRoomUseCase(request, subscriber)
      })

    router.post("/deleteRoom")
      .consumes(ContentType.APPLICATION_JSON)
      .consumes(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val subscriber = new DeleteRoomSubscriber(routingContext.response())
        deleteRoomUseCase(request, subscriber)
      })

    router.post("/joinRoom")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val subscriber = new JoinRoomSubscriber(routingContext.response())
        joinRoomUseCase(request, subscriber)
      })

    router.get("/rooms")
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = GetRoomsRequest()
        val subscriber = new GetRoomsSubscriber(routingContext.response())
        getRoomsUseCase(request, subscriber)
      })

    router.post("/joinRoom")
      .consumes("application/json")
      .produces("application/json")
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val checkSubscriber = new JoinRoomValiditySubscriber(routingContext.response(), request, joinRoomUseCase)
        joinRoomValidation(request, checkSubscriber)
      })
  }

  override def start(): Unit = startHttpServer(host, port)
    .doOnCompleted(
      publisher.publish(name = "room-service", host = host, port = port)
        .subscribe(record => log.info(s"${record.getName} record published!"),
          log.error(s"Could not publish record", _)))
    .subscribe(server => log.info(s"Server started at http://$host:${server.actualPort}"),
      log.error(s"Could not start server at http://$host:$port", _))

}

object RoomVerticle {

  object Implicits {

    implicit def jsonObjectToCreateUserRequest(json: JsonObject): CreateUserRequest =
      gson fromJsonObject[CreateUserRequest] json

    implicit def jsonObjectToCreateRoomRequest(json: JsonObject): CreateRoomRequest =
      gson fromJsonObject[CreateRoomRequest] json

    implicit def jsonObjectToDeleteRoomRequest(json: JsonObject): DeleteRoomRequest =
      gson fromJsonObject[DeleteRoomRequest] json

    implicit def jsonObjectToJoinRoomRequest(json: JsonObject): JoinRoomRequest =
      gson fromJsonObject[JoinRoomRequest] json

    implicit def jsonObjectToJoinRoomRequest(json: JsonObject): JoinRoomRequest =
      JoinRoomRequest(json.getString("name"), json.getString("username"))
  }

}
