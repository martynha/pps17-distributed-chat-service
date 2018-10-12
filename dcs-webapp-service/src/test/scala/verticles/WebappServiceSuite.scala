package verticles

import io.vertx.ext.unit.report.ReportOptions
import io.vertx.ext.unit.{TestOptions, TestSuite}
import io.vertx.lang.scala.ScalaLogger
import io.vertx.lang.scala.ScalaVerticle._
import io.vertx.scala.core.{Vertx, VertxOptions}
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.VertxHelper.Implicits._
import it.unibo.dcs.service.webapp.verticles.WebAppVerticle
import it.unibo.dcs.service.webapp.verticles.utils.DeploymentUtils._

import scala.language.postfixOps

object WebappServiceSuite extends App {

  private val logger = ScalaLogger.getLogger(getClass.getName)

  TestSuite.create("WebappService Test Suite")
    .test("WebApp Service is successfully deployed", context => {

      /* Verticle clustered deployment*/
      VertxHelper.toObservable[Vertx](Vertx.clusteredVertx(VertxOptions(), _))
        .subscribe(vertx => vertx.deployVerticle(nameForVerticle[WebAppVerticle], deploymentOptions,
          context.asyncAssertSuccess()), cause => logger.error("", cause))
    }).after(_ => System.exit(0))
    .run(new TestOptions().addReporter(new ReportOptions().setTo("console")))
}
