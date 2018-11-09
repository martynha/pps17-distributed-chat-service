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
import it.unibo.dcs.service.webapp.verticles.WebAppVerticle.{pathParamSeparator, roomsURI}
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
    /* Disable Cors to allow local requests */
    disableCors(router)
    /* Enable to serve static content */
    router.route().handler(StaticHandler.create())

    /* Api routes definition */
    val apiRouter = Router.router(vertx)
    apiRouter.route("/events/*").handler(sockJSHandler)
    defineServiceApi(apiRouter)
    router.mountSubRouter("/api", apiRouter)
  }

  private def defineServiceApi(apiRouter: Router): Unit = {
    implicit val ctx: core.Context = this.ctx

    userRegistrationRoute(apiRouter)
    loginRoute(apiRouter)
    logoutRoute(apiRouter)
    updateUserRoute(apiRouter)
    createRoomRoute(apiRouter)
    joinRoomRoute(apiRouter)
    leaveRoomRoute(apiRouter)
    deleteRoomRoute(apiRouter)
    getAllRoomsRoute(apiRouter)
    sendMessageRoute(apiRouter)
    getRoomParticipationsRoute(apiRouter)
    getUserParticipationsRoute(apiRouter)
    getUserRoute(apiRouter)
  }

  private def getUserRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.get(s"/users/:${ParamLabels.usernameLabel}")
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleGetUser context)
  }

  private def getUserParticipationsRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.get(s"/users/:${ParamLabels.usernameLabel}/participations")
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleGetUserParticipations context)
  }

  private def getRoomParticipationsRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.get(roomsURI + pathParamSeparator + ParamLabels.roomNameLabel + "/participations")
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleGetRoomParticipations context)
  }

  private def sendMessageRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.post(roomsURI + pathParamSeparator + ParamLabels.roomNameLabel + "/messages")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleSendMessage context)
  }

  private def getAllRoomsRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.get(roomsURI)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleGetRooms context)
  }

  private def deleteRoomRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.delete(roomsURI + pathParamSeparator + ParamLabels.roomNameLabel)
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleRoomDeletion context)
  }

  private def leaveRoomRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.delete(roomsURI + pathParamSeparator + ParamLabels.roomNameLabel + "/participations/:" + ParamLabels.userLabel)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleLeaveRoom context)
  }

  private def joinRoomRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.post(roomsURI + pathParamSeparator + ParamLabels.roomNameLabel + "/participations")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleJoinRoom context)
  }

  private def createRoomRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.post(roomsURI)
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleRoomCreation context)
  }

  private def updateUserRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.put("/users/:" + ParamLabels.userLabel)
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleUserEditing context)
  }

  private def logoutRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.delete("/logout")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleLogout context)
  }

  private def loginRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.post("/login")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleLogin context)
  }

  private def userRegistrationRoute(apiRouter: Router)(implicit ctx: core.Context) = {
    apiRouter.post("/register")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleRegistration context)
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

private[verticles] object WebAppVerticle {

  def roomsURI: String = "/rooms"

  def pathParamSeparator: String = "/:"
}
