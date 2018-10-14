package it.unibo.dcs.service.room

import io.vertx.scala.core.http.HttpServerResponse
import rx.lang.scala.Subscriber

package object subscriber {

  final class CreateRoomSubscriber (response: HttpServerResponse) extends Subscriber[Unit] {
    override def onNext(value: Unit): Unit = ()

    override def onCompleted(): Unit = response.end()

    override def onError(error: Throwable): Unit = ???
  }

  final class CreateUserSubscriber(response: HttpServerResponse) extends Subscriber[Unit] {

    override def onNext(value: Unit): Unit = ()

    override def onCompleted(): Unit = response.end()

    override def onError(error: Throwable): Unit = ???

  }

  class DeleteRoomSubscriber (response: HttpServerResponse) extends Subscriber[Unit] {
    override def onNext(value: Unit): Unit = ()

    override def onCompleted(): Unit = response.end()

    override def onError(error: Throwable): Unit = ???
  }

}
