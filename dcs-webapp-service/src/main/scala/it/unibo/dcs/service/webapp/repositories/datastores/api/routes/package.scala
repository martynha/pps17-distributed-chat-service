package it.unibo.dcs.service.webapp.repositories.datastores.api

package object routes {

  /* URIs */
  private val apiPrefix = "/api"

  val loginUserURI: String = apiPrefix + "/login"
  val registerUserURI: String = apiPrefix + "/register"
  val logoutUserURI: String = apiPrefix + "/protected/logout"
  val protectedRoomURI: String = apiPrefix + "/protected/room"
  val roomURI: String = apiPrefix + "/room"
  val usersURI: String = apiPrefix + "/user"

}
