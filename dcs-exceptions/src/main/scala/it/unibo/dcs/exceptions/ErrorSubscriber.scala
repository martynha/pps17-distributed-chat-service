package it.unibo.dcs.exceptions

import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.JsonHelper.Implicits.jsonObjectToString
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.exceptions.Implicits.{throwableToHttpResponseStatus, throwableToJsonObject}
import rx.lang.scala.Subscriber

trait ErrorSubscriber extends Subscriber[Nothing] {

  protected val response: HttpServerResponse

  override def onError(error: Throwable): Unit = {
    error.printStackTrace()
    val json: JsonObject = error
    response.setStatus(error).end(json)
  }

}
