package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import it.unibo.dcs.commons.Publisher
import it.unibo.dcs.service.webapp.interaction.Results.NotifyTypingUserResult
import rx.lang.scala.Subscriber

final class NotifyTypingInRoomSubscriber(private[this] val publisher: Publisher)
  extends Subscriber[NotifyTypingUserResult] {

  override def onNext(result: NotifyTypingUserResult): Unit = {
    publisher.publish(result.username)
  }

}

object NotifyTypingInRoomSubscriber {
  def apply(publisher: Publisher): NotifyTypingInRoomSubscriber =
    new NotifyTypingInRoomSubscriber(publisher)
}
