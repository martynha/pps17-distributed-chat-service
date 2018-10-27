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
import it.unibo.dcs.service.webapp.verticles.Addresses.rooms
import it.unibo.dcs.service.webapp.verticles.handler.ServiceRequestHandler
import org.apache.http.entity.ContentType
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
    router.route()
      .handler(BodyHandler.create())

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

    router.route().handler(StaticHandler.create())

    val apiRouter = Router.router(vertx)

    implicit val ctx: core.Context = this.ctx

    apiRouter.route("/events/*")
      .handler(sockJSHandler)

    apiRouter.post("/register")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleRegistration context)

    apiRouter.post("/login")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleLogin context)

    apiRouter.post("/logout")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleLogout context)

    apiRouter.post("/rooms")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleRoomCreation context)

    apiRouter.post("/rooms/:room")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleJoinRoom context)

    apiRouter.delete("/rooms")
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .handler(context => requestHandler handleRoomDeletion context)

    apiRouter.get("/rooms")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(context => requestHandler handleGetRooms context)

    router.mountSubRouter("/api", apiRouter)
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
      .addOutboundPermitted(PermittedOptions().setAddress(rooms.deleted))
      .addOutboundPermitted(PermittedOptions().setAddress(rooms.joined))

    SockJSHandler.create(vertx).bridge(options)
  }

}
