package it.unibo.dcs.service.webapp.model

import java.util.Date

case class Message (room: Room, username: String, content: String, timestamp: Date)
