package it.unibo.dcs.service.webapp.verticles

private[verticles] object Addresses {

  object Rooms {

    private val prefix = "rooms"

    val created = s"$prefix.created"

    val deleted = s"$prefix.deleted"

    val joined = s"$prefix.joined"

    val left = s"$prefix.leaved"

  }

  object Messages {
    private val prefix = "messages"

    val sent = s"$prefix.sent"
  }

}
