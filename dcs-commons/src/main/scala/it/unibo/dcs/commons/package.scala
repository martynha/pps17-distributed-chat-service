package it.unibo.dcs

import io.vertx.core.AsyncResult
import io.vertx.scala.core.eventbus.{DeliveryOptions, EventBus, Message}
import it.unibo.dcs.commons.VertxHelper.Implicits.functionToHandler

import scala.reflect.runtime.universe.TypeTag

package object commons {

  private[commons] trait EventBusObject {

    protected val eventBus: EventBus

    protected val address: String

  }

  sealed trait Consumer extends EventBusObject {

    final def apply[T: TypeTag](handler: Message[T] => Unit): Unit = eventBus.consumer[T](address, handler)

  }

  sealed trait Publisher extends EventBusObject {

    final def publish[T <: AnyRef](message: T, options: DeliveryOptions = DeliveryOptions()): Unit =
      eventBus.publish(address, message, options)

  }

  sealed trait Sender extends EventBusObject {

    final def send[T <: AnyRef, U: TypeTag](message: T, options: DeliveryOptions = DeliveryOptions(), replyHandler: AsyncResult[Message[U]] => Unit = null): Unit =
      eventBus.send(address, message, options, replyHandler)

  }

  final case class Address private[commons](protected override val eventBus: EventBus, protected override val address: String)
    extends Consumer with Publisher with Sender

}
