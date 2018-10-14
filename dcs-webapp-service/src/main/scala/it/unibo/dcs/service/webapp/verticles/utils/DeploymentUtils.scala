package it.unibo.dcs.service.webapp.verticles.utils

import java.net.InetAddress

import io.vertx.core.json.JsonObject
import io.vertx.scala.core.DeploymentOptions

/** It contains some useful methods to deploy the service. */
object DeploymentUtils {

  def deploymentOptions(port: Int): DeploymentOptions = {
    DeploymentOptions() setConfig serverConfiguration(port)
  }

  def serverConfiguration(port: Int): JsonObject = {
    new JsonObject()
      .put("host", InetAddress.getLocalHost.getHostAddress)
      .put("port", port)
  }
}
