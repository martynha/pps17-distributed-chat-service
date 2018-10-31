package it.unibo.dcs.service.room

import java.net.InetAddress

import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.{DeploymentOptions, Vertx, VertxOptions}
import io.vertx.scala.ext.jdbc.JDBCClient
import io.vertx.scala.ext.sql.SQLConnection
import io.vertx.servicediscovery.{Record, ServiceDiscovery}
import it.unibo.dcs.commons.VertxHelper.Implicits._
import it.unibo.dcs.commons.service.HttpEndpointPublisherImpl
import it.unibo.dcs.commons.service.codecs.RecordMessageCodec
import it.unibo.dcs.commons.{IoHelper, Logging, VertxHelper}
import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.data.impl.RoomDataStoreDatabase
import it.unibo.dcs.service.room.repository.impl.RoomRepositoryImpl

import scala.language.implicitConversions

object Launcher extends App with Logging {

  VertxHelper.toObservable[Vertx](Vertx.clusteredVertx(VertxOptions(), _))
    .flatMap { vertx =>
      val config = IoHelper.readJsonObject("/db_config.json")
      VertxHelper.toObservable[SQLConnection](JDBCClient.createNonShared(vertx, config).getConnection(_))
        .map((vertx, _))
    }
    .subscribe({
      case (vertx, connection) =>
        val discovery = ServiceDiscovery.create(vertx)
        val eventBus = vertx.eventBus
        vertx.eventBus.registerDefaultCodec[Record](new RecordMessageCodec())
        val publisher = new HttpEndpointPublisherImpl(discovery, eventBus)
        val roomDataStore: RoomDataStore = new RoomDataStoreDatabase(connection)
        val roomRepository = new RoomRepositoryImpl(roomDataStore)
        val config = new JsonObject()
          .put("host", InetAddress.getLocalHost.getHostAddress)
          .put("port", args(0).toInt)
        vertx.deployVerticle(new RoomVerticle(roomRepository, publisher), DeploymentOptions().setConfig(config))
    }, cause => log.error("Could not create Vert.x instance", cause))

}
