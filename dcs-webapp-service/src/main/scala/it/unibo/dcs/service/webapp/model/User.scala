package it.unibo.dcs.service.webapp.model

import java.util.Date

/** Model class that represents a User in the application domain
  *
  * @param username  username
  * @param firstName user first name
  * @param lastName  user last name
  * @param bio       user personal description
  * @param visible   boolean flag used to indicate if the user wants to be seen online or not
  * @param lastSeen  timestamp of the last access
  */
case class User(username: String, firstName: String, lastName: String, bio: String, visible: Boolean, lastSeen: Date)
