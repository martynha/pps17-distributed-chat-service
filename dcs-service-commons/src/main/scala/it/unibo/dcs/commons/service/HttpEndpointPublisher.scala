package it.unibo.dcs.commons.service

import io.vertx.core.json.JsonObject
import io.vertx.servicediscovery.Record
import it.unibo.dcs.commons.service.HttpEndpointPublisher._
import rx.lang.scala.Observable

trait HttpEndpointPublisher {

  def publish(name: String,
              ssl: Boolean = DEFAULT_SSL,
              host: String = DEFAULT_HOST,
              port: Int = DEFAULT_PORT,
              root: String = DEFAULT_ROOT,
              metadata: JsonObject = defaultMetadata): Observable[Record]

  def unpublish(record: Record): Observable[Record]

  def clear(): Observable[Unit]

}

object HttpEndpointPublisher {

  val DEFAULT_SSL = false
  val DEFAULT_HOST = "0.0.0.0"
  val DEFAULT_PORT = 80
  val DEFAULT_ROOT = "/"

  def defaultMetadata: JsonObject = new JsonObject()

}
