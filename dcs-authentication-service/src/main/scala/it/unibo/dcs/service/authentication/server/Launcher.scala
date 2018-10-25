package it.unibo.dcs.service.authentication.server

import java.net.InetAddress

import io.vertx.lang.scala.{ScalaLogger, VertxExecutionContext}
import io.vertx.lang.scala.json.Json
import io.vertx.scala.core.{DeploymentOptions, Vertx, VertxOptions}
import io.vertx.scala.ext.jdbc.JDBCClient
import io.vertx.scala.ext.sql.SQLConnection
import io.vertx.servicediscovery.{Record, ServiceDiscovery}
import it.unibo.dcs.service.authentication.data.AuthenticationDataStoreDatabase
import it.unibo.dcs.service.authentication.repository.AuthenticationRepositoryImpl
import it.unibo.dcs.commons.IoHelper
import it.unibo.dcs.commons.VertxHelper.Implicits._
import it.unibo.dcs.commons.service.HttpEndpointPublisherImpl
import it.unibo.dcs.commons.service.codecs.RecordMessageCodec

import scala.util.{Failure, Success}

/** Entry point of the application.
  * It launches the verticle associated with Authentication Service in clustered mode. */
object Launcher extends App {

  private val logger = ScalaLogger.getLogger(getClass.getName)

  Vertx.clusteredVertx(VertxOptions(), ar => {
    if (ar.succeeded) {
      val vertx = ar.result()
      val jdbcClient = JDBCClient.createNonShared(vertx, IoHelper.readJsonObject("/db_config.json"))
      jdbcClient.getConnectionFuture().onComplete {
        case Success(connection: SQLConnection) => deployVerticle(vertx, connection)
        case Failure(cause) => logger.error("", cause)
      }(VertxExecutionContext(vertx.getOrCreateContext()))
    }
  })

  private def deployVerticle(vertx: Vertx, sqlConnection: SQLConnection): Unit = {
    vertx.eventBus.registerDefaultCodec[Record](new RecordMessageCodec())

    val authRepository = new AuthenticationRepositoryImpl(new AuthenticationDataStoreDatabase(sqlConnection))
    val discovery = ServiceDiscovery.create(vertx)
    val publisher = new HttpEndpointPublisherImpl(discovery, vertx.eventBus)
    val config = Json.obj(("host", InetAddress.getLocalHost.getHostAddress), ("port", args(0).toInt))
    vertx deployVerticle(new AuthenticationVerticle(authRepository, publisher), DeploymentOptions().setConfig(config))
  }

}
