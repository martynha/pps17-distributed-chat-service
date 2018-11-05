package it.unibo.dcs.service.webapp.verticles

import java.net.InetAddress

import io.vertx.core.{AsyncResult, Handler}
import io.vertx.ext.unit.report.ReportOptions
import io.vertx.ext.unit.{TestContext, TestOptions, TestSuite}
import io.vertx.lang.scala.ScalaVerticle._
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.{DeploymentOptions, Vertx, VertxOptions}
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.VertxHelper.Implicits._
import it.unibo.dcs.commons.logging.Logging

import scala.language.postfixOps

object WebappVerticleSuite extends App with Logging {

  val deploymentOptions = DeploymentOptions().setConfig(new JsonObject()
    .put("host", InetAddress.getLocalHost.getHostAddress))

  TestSuite.create("WebappService Test Suite")
    .test("WebApp Service is successfully deployed", context => {
      /* Verticle clustered deployment*/
      VertxHelper.toObservable[Vertx](Vertx.clusteredVertx(VertxOptions(), _))
        .subscribe(vertx => {
          vertx.deployVerticle(nameForVerticle[WebAppVerticle], deploymentOptions,
            verticleClosingHandler(vertx, context))
        }, cause => log.error("", cause))
    })
    .run(new TestOptions().addReporter(new ReportOptions().setTo("console")))


  private def verticleClosingHandler(vertx: Vertx, context: TestContext): Handler[AsyncResult[String]] = ar => {
    if (ar.succeeded()) {
      vertx.close()
    } else {
      context.fail(ar.cause())
    }
  }
}
