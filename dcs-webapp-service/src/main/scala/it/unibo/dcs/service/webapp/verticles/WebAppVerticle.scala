package it.unibo.dcs.service.webapp.verticles

import io.vertx.core.http.HttpMethod._
import io.vertx.core.{AbstractVerticle, Context, Vertx}
import io.vertx.scala.core
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.{BodyHandler, CorsHandler}
import io.vertx.servicediscovery.{Record, ServiceDiscovery}
import it.unibo.dcs.commons.service.codecs.RecordMessageCodec
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, HttpEndpointPublisherImpl, ServiceVerticle}
import it.unibo.dcs.service.webapp.verticles.handler.ServiceRequestHandler

/** Verticle that runs the WebApp Service */
final class WebAppVerticle extends ServiceVerticle {

  private var host: String = _
  private var port: Int = _

  private var publisher: HttpEndpointPublisher = _
  private var requestHandler: ServiceRequestHandler = _

  override def init(jVertx: Vertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)

    initVerticleConfiguration(context)
    val discovery = ServiceDiscovery.create(jVertx)
    val eventBus: EventBus = initEventBus

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

    val apiRouter = Router.router(vertx)

    implicit val ctx: core.Context = this.ctx

    apiRouter.route()
      .handler(BodyHandler.create())

    apiRouter.route().handler(CorsHandler.create("*")
      .allowedMethod(GET)
      .allowedMethod(POST)
      .allowedMethod(PATCH)
      .allowedMethod(PUT)
      .allowedMethod(DELETE)
      .allowedHeader("Access-Control-Allow-Method")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Credentials")
      .allowedHeader("Content-Type"))

    apiRouter.post("/register")
      .consumes("application/json")
      .produces("application/json")
      .handler(context => requestHandler handleRegistration context)

    apiRouter.post("/login")
      .consumes("application/json")
      .produces("application/json")
      .handler(context => requestHandler handleLogin context)

    apiRouter.post("/logout")
      .consumes("application/json")
      .produces("application/json")
      .handler(context => requestHandler handleLogout context)

    apiRouter.post("/rooms")
      .consumes("application/json")
      .produces("application/json")
      .handler(context => requestHandler handleRoomCreation context)

    apiRouter.delete("/rooms")
      .consumes("application/json")
      .produces("application/json")
      .handler(context => requestHandler handleRoomDeletion context)

    router.mountSubRouter("/api", apiRouter)
  }

  override def start(): Unit = {
    startHttpServer(host, port)
      .doOnCompleted(
        publisher.publish("webapp-service")
          .subscribe(record => log.info(s"${record.getName} record published!"),
            log.error(s"Could not publish record", _)))
      .subscribe(server => log.info(s"Server started at http://$host:${server.actualPort}"),
        log.error(s"Could not start server at http://$host:$port", _))
  }

}


