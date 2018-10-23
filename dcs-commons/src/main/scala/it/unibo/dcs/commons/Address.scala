package it.unibo.dcs.commons

import io.vertx.core.AsyncResult
import io.vertx.scala.core.eventbus._
import it.unibo.dcs.commons.VertxHelper.Implicits.functionToHandler

import scala.reflect.runtime.universe.TypeTag

final class Address private[commons](private[this] val eventBus: EventBus, private[this] val name: String) {

  def send[T: TypeTag](message: AnyRef, options: DeliveryOptions = DeliveryOptions(), replyHandler: AsyncResult[Message[T]] => Unit = null): Address = {
    eventBus.send(name, message, options, replyHandler)
    this
  }

  def publish(message: AnyRef, options: DeliveryOptions = DeliveryOptions()): Address = {
    eventBus.publish(name, message, options)
    this
  }

  def consumer[T: TypeTag]: MessageConsumer[T] = eventBus.consumer[T](name)

  def consumer[T: TypeTag](handler: Message[T] => Unit): MessageConsumer[T] = eventBus.consumer[T](name, handler)

  def localConsumer[T: TypeTag]: MessageConsumer[T] = eventBus.localConsumer[T](name)

  def localConsumer[T: TypeTag](handler: Message[T] => Unit): MessageConsumer[T] = eventBus.localConsumer[T](name, handler)

  def sender[T: TypeTag](options: DeliveryOptions = DeliveryOptions()): MessageProducer[T] = eventBus.sender[T](name, options)

  def publisher[T: TypeTag](options: DeliveryOptions = DeliveryOptions()): MessageProducer[T] = eventBus.publisher[T](name, options)

}
