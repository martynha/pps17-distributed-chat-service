package it.unibo.dcs.service.webapp.verticles

import io.vertx.core.http.HttpMethod._
import io.vertx.core.{AbstractVerticle, Context, Vertx}
import io.vertx.scala.core
import io.vertx.scala.ext.bridge.PermittedOptions
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.sockjs.{BridgeOptions, SockJSHandler}
import io.vertx.scala.ext.web.handler.{BodyHandler, CorsHandler, StaticHandler}
import io.vertx.servicediscovery.ServiceDiscovery
import it.unibo.dcs.commons.VertxWebHelper.Implicits.contentTypeToString
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, HttpEndpointPublisherImpl, ServiceVerticle}
import it.unibo.dcs.service.webapp.interaction.Labels._
import it.unibo.dcs.service.webapp.verticles.Addresses.{Messages, Rooms}
import it.unibo.dcs.service.webapp.verticles.handler.ServiceRequestHandler
import org.apache.http.entity.ContentType._

/** Verticle that runs the WebApp Service */
final class WebAppVerticle extends ServiceVerticle {

  @SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.Null"))
  private var host: String = _
  @SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.Null"))
  private var port: Int = _

  @SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.Null"))
  private var publisher: HttpEndpointPublisher = _
  @SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.Null"))
  private var requestHandler: ServiceRequestHandler = _

  private val serviceRecordName = "webapp-service"

  override def init(jVertx: Vertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)

    initVerticleConfiguration(context)
    val discovery = ServiceDiscovery.create(jVertx)
    initEventBus

    this.publisher = new HttpEndpointPublisherImpl(discovery, eventBus)
    this.requestHandler = ServiceRequestHandler(jVertx, eventBus)
  }

  private def initVerticleConfiguration(context: Context): Unit = {
    val config = context.config()
    this.host = config.getString("host")
    this.port = config.getInteger("port")
  }

  private def initEventBus = {
    val eventBus = vertx.eventBus
    eventBus
  }

  override protected def initializeRouter(router: Router): Unit = {
    /* Enables the fetching of request bodies */
    router.route().handler(BodyHandler.create())

    disableCors(router)

    router.route().handler(StaticHandler.create())

    val apiRouter = Router.router(vertx)

    apiRouter.route("/events/*").handler(sockJSHandler)

    defineServiceApi(apiRouter)
    router.mountSubRouter("/api", apiRouter)
  }

  private def defineServiceApi(apiRouter: Router): Unit = {
    implicit val ctx: core.Context = this.ctx

    apiRouter.post("/register")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleRegistration context)

    apiRouter.post("/login")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleLogin context)

    apiRouter.delete("/logout")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleLogout context)

    apiRouter.put("/users/:" + ParamLabels.userLabel)
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleUserEditing context)

    apiRouter.post("/rooms")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleRoomCreation context)

    apiRouter.post("/rooms/:" + ParamLabels.roomNameLabel + "/participations")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleJoinRoom context)

    apiRouter.delete("/rooms/:" + ParamLabels.roomNameLabel + "/participations/:" + ParamLabels.userLabel)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleLeaveRoom context)

    apiRouter.delete("/rooms/:" + ParamLabels.roomNameLabel)
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleRoomDeletion context)

    apiRouter.get("/rooms")
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleGetRooms context)

    apiRouter.post("/rooms/:" + ParamLabels.roomNameLabel + "/messages")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleSendMessage context)

    apiRouter.get("/rooms/:" + ParamLabels.roomNameLabel + "/participations")
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleGetRoomParticipations context)
      
    apiRouter.get(s"/users/:${ParamLabels.usernameLabel}/participations")
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleGetUserParticipations context)
  }
    
  private def disableCors(router: Router) = {
    router.route().handler(CorsHandler.create("*")
      .allowedMethod(GET)
      .allowedMethod(POST)
      .allowedMethod(PATCH)
      .allowedMethod(PUT)
      .allowedMethod(DELETE)
      .allowedHeader("Access-Control-Allow-Method")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Credentials")
      .allowedHeader("Content-Type")
      .allowedHeader("Authorization"))
  }

  override def start(): Unit = {
    startHttpServer(host, port)
      .doOnCompleted(
        publisher.publish(serviceRecordName)
          .subscribe(record => log.info(s"${record.getName} record published!"),
            log.error(s"Could not publish record", _)))
      .subscribe(server => log.info(s"Server started at http://$host:${server.actualPort}"),
        log.error(s"Could not start server at http://$host:$port", _))
  }

  private lazy val sockJSHandler: SockJSHandler = {
    val options = BridgeOptions()
      .addOutboundPermitted(PermittedOptions().setAddress(Rooms.deleted))
      .addOutboundPermitted(PermittedOptions().setAddress(Rooms.joined))
      .addOutboundPermitted(PermittedOptions().setAddress(Messages.sent))
      .addOutboundPermitted(PermittedOptions().setAddress(Rooms.left))
      .addOutboundPermitted(PermittedOptions().setAddress(Rooms.created))

    SockJSHandler.create(vertx).bridge(options)
  }

}
