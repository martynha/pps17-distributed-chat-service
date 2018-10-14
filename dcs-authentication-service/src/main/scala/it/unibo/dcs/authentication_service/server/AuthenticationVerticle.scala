package it.unibo.dcs.authentication_service.server

import io.vertx.core
import io.vertx.core.http.HttpMethod._
import io.vertx.core.{AbstractVerticle, Context}
import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.auth.jwt.{JWTAuth, JWTAuthOptions}
import io.vertx.scala.ext.web.handler.{BodyHandler, CorsHandler, JWTAuthHandler}
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.dcs.authentication_service.interactor.{LoginUserUseCase, LogoutUserUseCase, RegisterUserUseCase}
import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.PostExecutionThread
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, ServiceVerticle}
import rx.lang.scala.Subscriber

import scala.io.Source

final class AuthenticationVerticle(authenticationRepository: AuthenticationRepository, private[this] val publisher: HttpEndpointPublisher)
  extends ServiceVerticle {

  private var host: String = _
  private var port: Int = _

  override protected def initializeRouter(router: Router): Unit = {
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

    val authProvider = createJwtAuthProvider()
    router.route().handler(BodyHandler.create())
    setupRedirects(router, authProvider)
    setupRoutes(router, authProvider)
  }

  override def init(jVertx: core.Vertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)
    host = config getString "host"
    port = config getInteger "port"
  }

  override def start(): Unit = startHttpServer(host, port)
    .doOnCompleted(
      publisher.publish(name = "authentication-service", host = host, port = port)
        .subscribe(record => log.info(s"${record.getName} record published!"),
          log.error(s"Could not publish record", _)))
    .subscribe(server => log.info(s"Server started at http://$host:${server.actualPort}"),
      log.error(s"Could not start server at http://$host:$port", _))

  private def createJwtAuthProvider(): JWTAuth = {
    val keyStoreSecret = Source.fromResource("keystore-secret.txt").getLines().next()
    val keyStoreOptions = Json.obj(("type", "jceks"), ("path", "keystore.jceks"), ("password", keyStoreSecret))
    val authOptions = JWTAuthOptions.fromJson(Json.obj(("keyStore", keyStoreOptions)))
    JWTAuth.create(vertx, authOptions)
  }

  private def setupRedirects(router: Router, authProvider: JWTAuth): Unit = {
    router.route("/protected/*").handler(JWTAuthHandler.create(authProvider))
    val filterRouter = Router.router(vertx)
    setupFilterRouter(filterRouter)
    filterRouter.mountSubRouter("/protected/", router)
  }

  private def setupFilterRouter(filterRouter: Router): Unit = {
    filterRouter.get.handler(context => {
      val token = getTokenFromHeader(context)
      if (token.isEmpty) {
        respondWithCode(401)(context)
      }
      authenticationRepository.isTokenInvalid(token.get).subscribe(getTokenSubscriber(context))
    })
  }

  private def getTokenSubscriber(implicit context: RoutingContext): Subscriber[Boolean] = {
    new Subscriber[Boolean]() {
      override def onNext(value: Boolean): Unit = if (value) {
        context.next()
      } else {
        respondWithCode(401)
      }

      override def onError(error: Throwable): Unit = respondWithCode(401)
    }
  }

  private def setupRoutes(router: Router, jwtAuth: JWTAuth): Unit = {
    val requestHandler = getRequestHandler(jwtAuth)
    router.post("/register").handler(requestHandler.handleRegistration(_))
    router.post("/login").handler(requestHandler.handleLogin(_))
    router.post("/protected/logout").handler(requestHandler.handleLogout(_))
  }

  private def getRequestHandler(jwtAuth: JWTAuth): ServiceRequestHandler = {
    val threadExecutor = ThreadExecutorExecutionContext(vertx)
    val postExecutionThread = PostExecutionThread(RxHelper.scheduler(this.ctx))
    val loginUseCase = new LoginUserUseCase(threadExecutor, postExecutionThread, authenticationRepository, jwtAuth)
    val logoutUseCase = new LogoutUserUseCase(threadExecutor, postExecutionThread, authenticationRepository)
    val registerUseCase = new RegisterUserUseCase(threadExecutor, postExecutionThread, authenticationRepository, jwtAuth)
    new ServiceRequestHandlerImpl(loginUseCase, logoutUseCase, registerUseCase)
  }
}
