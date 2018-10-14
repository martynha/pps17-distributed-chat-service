package it.unibo.dcs.authentication_service.server

import java.net.InetAddress

import io.vertx.core.eventbus.{EventBus => JEventBus}
import io.vertx.lang.scala.VertxExecutionContext
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.{DeploymentOptions, Vertx, VertxOptions}
import io.vertx.scala.ext.jdbc.JDBCClient
import io.vertx.scala.ext.sql.SQLConnection
import io.vertx.servicediscovery.{Record, ServiceDiscovery}
import it.unibo.dcs.authentication_service.data.AuthenticationDataStoreDatabase
import it.unibo.dcs.authentication_service.repository.AuthenticationRepositoryImpl
import it.unibo.dcs.commons.IoHelper
import it.unibo.dcs.commons.service.HttpEndpointPublisherImpl
import it.unibo.dcs.commons.VertxHelper.Implicits._
import it.unibo.dcs.commons.service.codecs.RecordMessageCodec

import scala.util.{Failure, Success}

object Launcher extends App {
  Vertx.clusteredVertx(VertxOptions(), ar => {
    if (ar.succeeded) {
      val vertx = ar.result()
      implicit val executionContext: VertxExecutionContext = VertxExecutionContext(vertx.getOrCreateContext())
      val config = IoHelper.readJsonObject("/db_config.json")
      val jdbcClient = JDBCClient.createNonShared(vertx, config)
      jdbcClient.getConnectionFuture().onComplete {
        case Success(connection: SQLConnection) => deployVerticle(vertx, connection)
        case Failure(cause) => println(s"$cause")
      }
    }
  })

  private def deployVerticle(vertx: Vertx, sqlConnection: SQLConnection): Unit = {
    val authDataStore = new AuthenticationDataStoreDatabase(sqlConnection)
    val authRepository = new AuthenticationRepositoryImpl(authDataStore)


    vertx.eventBus.asJava.asInstanceOf[JEventBus].registerDefaultCodec(classOf[Record], new RecordMessageCodec())

    val discovery = ServiceDiscovery.create(vertx)
    val publisher = new HttpEndpointPublisherImpl(discovery, vertx.eventBus)

    val config = new JsonObject()
      .put("host", InetAddress.getLocalHost.getHostAddress)
      .put("port", args(0).toInt)
    vertx deployVerticle(new AuthenticationVerticle(authRepository, publisher), DeploymentOptions().setConfig(config))
  }

}
