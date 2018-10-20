package it.unibo.dcs.authentication_service.server

import io.vertx.core
import io.vertx.core.http.HttpMethod._
import io.vertx.core.{AbstractVerticle, Context}
import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.auth.jwt.{JWTAuth, JWTAuthOptions}
import io.vertx.scala.ext.web.handler.{BodyHandler, CorsHandler, JWTAuthHandler}
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.dcs.authentication_service.interactor._
import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.authentication_service.validator.LogoutUserValidator
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.PostExecutionThread
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, ServiceVerticle}

import scala.io.Source
import scala.util.{Failure, Success}

/** Verticle that runs the Authentication Service */
final class AuthenticationVerticle(authenticationRepository: AuthenticationRepository,
                                   private[this] val publisher: HttpEndpointPublisher) extends ServiceVerticle {

  private var host: String = _
  private var port: Int = _

  override protected def initializeRouter(router: Router): Unit = {
    router.route().handler(BodyHandler.create())
    setupCors(router)
    val authProvider = JWTAuth.create(vertx, createJwtAuthOptions())
    router.route().handler(BodyHandler.create())
    setupProtectedRoutes(router, authProvider)
    setupRoutes(router, authProvider)
  }

  private def setupCors(router: Router): Unit =
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

  private def createJwtAuthOptions(): JWTAuthOptions = {
    val keyStoreSecret = Source.fromResource("keystore-secret.txt").getLines().next()
    val keyStoreOptions = Json.obj(("type", "jceks"), ("path", "keystore.jceks"), ("password", keyStoreSecret))
    JWTAuthOptions.fromJson(Json.obj(("keyStore", keyStoreOptions)))
  }

  private def setupProtectedRoutes(router: Router, jwtAuth: JWTAuth): Unit = {
    val jwtAuthHandler = JWTAuthHandler.create(jwtAuth)
    val protectedRouter = Router.router(vertx)
    protectedRouter.route("/*").handler(protectedRouteHandler(jwtAuthHandler, jwtAuth)(_))
    router.mountSubRouter("/protected", protectedRouter)
  }

  private def protectedRouteHandler(jwtAuthHandler: JWTAuthHandler, jwtAuth: JWTAuth)
                                   (implicit context: RoutingContext): Unit =
    getTokenFromHeader.fold(respondWithCode(401))((jwtToken: String) =>
      authenticationRepository.isTokenValid(jwtToken)
        .subscribe(tokenValid => checkTokenValidityInDb(tokenValid, jwtAuthHandler, jwtAuth),
          _ => respondWithCode(401)))

  private def checkTokenValidityInDb(isTokenValid: Boolean, jwtAuthHandler: JWTAuthHandler, jwtAuth: JWTAuth)
                                    (implicit context: RoutingContext): Unit =
    if (isTokenValid) {
      checkTokenValidity(jwtAuthHandler, jwtAuth)
    } else {
      respondWithCode(401)
    }

  private def checkTokenValidity(jwtAuthHandler: JWTAuthHandler, jwtAuth: JWTAuth)
                                (implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    token.fold[Unit](() => respondWithCode(401))(tokenValue => {
      jwtAuth.authenticateFuture(Json.obj(("jwt", tokenValue))).onComplete {
        case Success(_) => context.next()
        case Failure(_) => respondWithCode(401)
      }
    })
  }

  private def setupRoutes(router: Router, jwtAuth: JWTAuth): Unit = {
    val requestHandler = getRequestHandler(jwtAuth)
    router.post("/register").handler(requestHandler.handleRegistration(_))
    router.post("/login").handler(requestHandler.handleLogin(_))
    router.post("/validateLogout").handler(requestHandler.handleCheckLogout(_))
    router.post("/protected/logout").handler(requestHandler.handleLogout(_))
    router.get("/protected/tokenValidity").handler(requestHandler.handleTokenCheck(_))
  }

  private def getRequestHandler(jwtAuth: JWTAuth): ServiceRequestHandler = {
    val threadExecutor = ThreadExecutorExecutionContext(vertx)
    val postExecutionThread = PostExecutionThread(RxHelper.scheduler(this.ctx))
    val loginUseCase = LoginUserUseCase(threadExecutor, postExecutionThread, authenticationRepository, jwtAuth)
    val logoutUseCase = LogoutUserUseCase(threadExecutor, postExecutionThread, authenticationRepository)
    val registerUseCase = RegisterUserUseCase(threadExecutor, postExecutionThread, authenticationRepository, jwtAuth)
    val checkTokenUseCase = CheckTokenUseCase(threadExecutor, postExecutionThread, authenticationRepository)
    val logoutValidator = LogoutUserValidator(authenticationRepository)
    val logoutUserValidation = LogoutUserValidation(threadExecutor, postExecutionThread, logoutValidator)
    ServiceRequestHandlerImpl(loginUseCase, logoutUseCase, registerUseCase, checkTokenUseCase, logoutUserValidation)
  }
}

object AuthenticationVerticle {
  def apply(authenticationRepository: AuthenticationRepository, publisher: HttpEndpointPublisher) =
    new AuthenticationVerticle(authenticationRepository, publisher)
}
