package it.unibo.dcs.service.webapp

import io.vertx.lang.scala.ScalaLogger
import io.vertx.scala.core.{Vertx, VertxOptions}
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.VertxHelper.Implicits._
import it.unibo.dcs.service.webapp.verticles.WebAppVerticle
import it.unibo.dcs.service.webapp.verticles.utils.DeploymentUtils.deploymentOptions

import scala.language.implicitConversions

/** Entry point of the application.
  * It launches the verticle associated with WebAppService in clustered mode. */
object Launcher extends App {

  private val logger = ScalaLogger.getLogger(getClass.getName)

  VertxHelper.toObservable[Vertx](Vertx.clusteredVertx(VertxOptions(), _))
    .subscribe(vertx => vertx deployVerticle(new WebAppVerticle, deploymentOptions(args(0).toInt)),
      cause => logger.error("", cause))

}
