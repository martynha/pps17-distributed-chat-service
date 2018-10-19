package it.unibo.dcs.service.webapp.verticles.utils

import io.vertx.core.json.JsonObject
import io.vertx.scala.core.DeploymentOptions
import it.unibo.dcs.commons.IoHelper

/** It contains some useful methods to deploy the service. */
object DeploymentUtils {

  /** It retrieves the deployment options to start the service
    *
    * @return Vertx verticle deployment options */
  def deploymentOptions: DeploymentOptions = {
    DeploymentOptions() setConfig serverConfiguration
  }


  /** It retrieves the host used to deploy the service
    *
    * @return service host */
  def deploymentHost: String = {
    deploymentOptions.getConfig.getString("host")
  }

  /** It retrieves the port used to deploy the service
    *
    * @return service port */
  def deploymentPort: Integer = {
    deploymentOptions.getConfig.getInteger("port")
  }

  /** It retrieves the server configuration expressed as Json Object
    *
    * @return the server configuration as json object */
  def serverConfiguration: JsonObject = {
    IoHelper.readJsonObject("/config.json")
  }
}
