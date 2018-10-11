package it.unibo.dcs.service.room

import io.vertx.core.{AbstractVerticle, Context, Vertx => JVertx}
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.BodyHandler
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.PostExecutionThread
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, ServiceVerticle}
import it.unibo.dcs.service.room.interactor.{CreateRoomUseCase, CreateUserUseCase, DeleteRoomUseCase}
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.{CreateRoomRequest, CreateUserRequest, DeleteRoomRequest}
import it.unibo.dcs.service.room.subscriber.{CreateRoomSubscriber, CreateUserSubscriber}

final class RoomVerticle(private[this] val roomRepository: RoomRepository, val publisher: HttpEndpointPublisher) extends ServiceVerticle {

  private[this] var deleteRoomUseCase: DeleteRoomUseCase = _
  private[this] var createUserUseCase: CreateUserUseCase = _
  private[this] var createRoomUseCase: CreateRoomUseCase = _

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
    deleteRoomUseCase = new DeleteRoomUseCase(threadExecutor, postExecutionThread, roomRepository)
  }

  override protected def initializeRouter(router: Router): Unit = {
    router.route().handler(BodyHandler.create())

    router.post("/createUser")
      .consumes("application/json")
      .produces("application/json")
      .handler(routingContext => {
        val username = routingContext.getBodyAsJson.get.getString("username")
        val request = CreateUserRequest(username)
        createUserUseCase(request, new CreateUserSubscriber(routingContext.response()))
      })

    router.post("/createRoom")
      .consumes("application/json")
      .produces("application/json")
      .handler(routingContext => {
        val name = routingContext.getBodyAsJson.get.getString("name")
        val username = routingContext.getBodyAsJson.get.getString("username")
        val request = CreateRoomRequest(name, username)
        createRoomUseCase(request, new CreateRoomSubscriber(routingContext.response()))
      })

    router.post("/deleteRoom")
      .consumes("application/json")
      .produces("application/json")
      .handler(routingContext => {
        val name = routingContext.getBodyAsJson.get.getString("name")
        val username = routingContext.getBodyAsJson.get.getString("username")
        val request = DeleteRoomRequest(name, username)
        deleteRoomUseCase(request).subscribe()
      })
  }

  override def start(): Unit = startHttpServer(host, port)
    .doOnCompleted(
      publisher.publish(name = "room-service", host = host, port = port)
        .subscribe(_ => println("Record published!"),
                   cause => println(s"Could not publish record: ${cause.getMessage}")))
    .subscribe(server => println(s"Server started at http://$host:${server.actualPort}"),
               cause => println(s"Could not start server at http://$host:$port: ${cause.getMessage}"))

}
