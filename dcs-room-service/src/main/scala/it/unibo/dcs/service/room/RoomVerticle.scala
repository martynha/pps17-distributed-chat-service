package it.unibo.dcs.service.room

import io.vertx.core.http.HttpMethod._
import io.vertx.core.{AbstractVerticle, Context, Vertx => JVertx}
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.web.{Router, RoutingContext}
import io.vertx.scala.ext.web.handler.{BodyHandler, CorsHandler}
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.PostExecutionThread
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, ServiceVerticle}
import it.unibo.dcs.service.room.RoomVerticle.Implicits._
import it.unibo.dcs.service.room.interactor.usecases.{CreateRoomUseCase, CreateUserUseCase, DeleteRoomUseCase}
import it.unibo.dcs.service.room.interactor.validations.{CreateRoomValidation, CreateUserValidation, DeleteRoomValidation}
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest}
import it.unibo.dcs.service.room.subscriber.{CreateRoomValiditySubscriber, CreateUserValiditySubscriber, DeleteRoomValiditySubscriber}
import it.unibo.dcs.service.room.validator.{CreateRoomValidator, CreateUserValidator, DeleteRoomValidator}

import scala.language.implicitConversions

final class RoomVerticle(private[this] val roomRepository: RoomRepository, val publisher: HttpEndpointPublisher) extends ServiceVerticle {

  private[this] var deleteRoomUseCase: DeleteRoomUseCase = _
  private[this] var createUserUseCase: CreateUserUseCase = _
  private[this] var createRoomUseCase: CreateRoomUseCase = _

  private[this] var deleteRoomValidation: DeleteRoomValidation = _
  private[this] var createRoomValidation: CreateRoomValidation = _
  private[this] var createUserValidation: CreateUserValidation = _

  private[this] var host: String = _
  private[this] var port: Int = _

  override def init(jVertx: JVertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)
    val config = context.config
    host = config.getString("host")
    port = config.getInteger("port")

    val threadExecutor = ThreadExecutorExecutionContext(vertx)
    val postExecutionThread = PostExecutionThread(RxHelper.scheduler(this.ctx))

    createUserUseCase = new CreateUserUseCase(threadExecutor, postExecutionThread, roomRepository)
    createRoomUseCase = new CreateRoomUseCase(threadExecutor, postExecutionThread, roomRepository)
    deleteRoomUseCase = new DeleteRoomUseCase(threadExecutor, postExecutionThread, roomRepository)

    val deleteRoomValidator = DeleteRoomValidator()
    val createRoomValidator = CreateRoomValidator()
    val createUserValidator = CreateUserValidator()

    deleteRoomValidation = new DeleteRoomValidation(threadExecutor, postExecutionThread, deleteRoomValidator)
    createRoomValidation = new CreateRoomValidation(threadExecutor, postExecutionThread, createRoomValidator)
    createUserValidation = new CreateUserValidation(threadExecutor, postExecutionThread, createUserValidator)
  }

  override protected def initializeRouter(router: Router): Unit = {
    router.route().handler(BodyHandler.create())

    router.route().handler(CorsHandler.create("*")
      .allowedMethod(GET)
      .allowedMethod(POST)
      .allowedMethod(PATCH)
      .allowedMethod(PUT)
      .allowedMethod(DELETE)
      .allowedHeader("Access-Control-Allow-Method")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Credentials")
      .allowedHeader("Content-Type"))

    router.post("/createUser")
      .consumes("application/json")
      .produces("application/json")
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val checkSubscriber = new CreateUserValiditySubscriber(routingContext.response(), request, createUserUseCase)
        createUserValidation(request, checkSubscriber)
      })

    router.post("/createRoom")
      .consumes("application/json")
      .produces("application/json")
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val checkSubscriber = new CreateRoomValiditySubscriber(routingContext.response(), request, createRoomUseCase)
        createRoomValidation(request, checkSubscriber)
      })

    router.post("/deleteRoom")
      .consumes("application/json")
      .produces("application/json")
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val checkSubscriber = new DeleteRoomValiditySubscriber(routingContext.response(), request, deleteRoomUseCase)
        deleteRoomValidation(request, checkSubscriber)
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
      CreateUserRequest(json.getString("username"))

    implicit def jsonObjectToCreateRoomRequest(json: JsonObject): CreateRoomRequest =
      CreateRoomRequest(json.getString("name"), json.getString("username"))

    implicit def jsonObjectToDeleteRoomRequest(json: JsonObject): DeleteRoomRequest =
      DeleteRoomRequest(json.getString("name"), json.getString("username"))

  }

}
