package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.lang.scala.json.JsonObject
import it.unibo.dcs.commons.VertxWebHelper.Implicits.jsonObjectToString
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.LoginResult
import rx.lang.scala.Subscriber

import it.unibo.dcs.service.webapp.interaction.Results.Implicits.loginResultToJsonObject

import scala.language.postfixOps

final class LoginUserSubscriber(protected override val response: HttpServerResponse) extends Subscriber[LoginResult]
  with ErrorSubscriber {

  override def onNext(result: LoginResult): Unit = {
    val json: JsonObject = result
    response.end(json)
  }

}

object LoginUserSubscriber {

  def apply(response: HttpServerResponse): LoginUserSubscriber = new LoginUserSubscriber(response)

}
