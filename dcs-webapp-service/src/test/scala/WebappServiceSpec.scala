import io.vertx.ext.unit.TestSuite
import io.vertx.lang.scala.ScalaVerticle.nameForVerticle
import io.vertx.scala.core.Vertx
import it.unibo.dcs.service.webapp.verticles.WebAppVerticle
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Promise
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class WebappServiceSpec extends FlatSpec with Matchers {

  /* FlatSpec style */
  "WebappVerticle" should "bind to 8080" in { // <2>
    val promise = Promise[String] // <3>

    Vertx.vertx().createHttpClient() // <4>
      .getNow(8080, "localhost", "/",
      r => {
        r.bodyHandler(b => promise.success(b.toString))
      })

    promise.future.map(res => res should equal("...")) // <5>
  }

  /* Vertx Unit style */
  val testSuite: TestSuite = TestSuite.create("WebappService Test Suite")
  testSuite.beforeEach(_ => {
    Vertx vertx() deployVerticle nameForVerticle[WebAppVerticle]
  }).test("first", context => {
    val async1 = context.async
    val client = Vertx vertx() createHttpClient
    val req = client.get(8080, "localhost", "/")
    req.exceptionHandler((err) => context.fail(err.getMessage))
    req.handler((resp) => {
      context.assertEquals(200, resp.statusCode)
      async1.complete()
    })
  }).afterEach(_ => {
    Vertx vertx() undeploy Vertx.vertx().deploymentIDs().head
  })
}
