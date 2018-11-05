package it.unibo.dcs.service.webapp.verticles

private[verticles] object Addresses {

  object Rooms {

    private val prefix = "rooms"

    val deleted = s"$prefix.deleted"

    val joined = s"$prefix.joined"

  }

  object Messages {
    private val prefix = "messages"

    val sent = s"$prefix.sent"
  }

}
