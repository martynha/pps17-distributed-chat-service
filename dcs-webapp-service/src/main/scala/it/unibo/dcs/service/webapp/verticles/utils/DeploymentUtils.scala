package it.unibo.dcs.service.webapp.verticles.utils

import io.vertx.core.json.JsonObject
import io.vertx.scala.core.DeploymentOptions
import it.unibo.dcs.commons.IoHelper

object DeploymentUtils {

  def deploymentOptions: DeploymentOptions = {
    DeploymentOptions() setConfig serverConfiguration
  }

  def deploymentHost: String = {
    deploymentOptions.getConfig.getString("host")
  }

  def deploymentPort: Integer = {
    deploymentOptions.getConfig.getInteger("port")
  }

  def serverConfiguration: JsonObject = {
    IoHelper.readJsonObject("/config.json")
  }
}
