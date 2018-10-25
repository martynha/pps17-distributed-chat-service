package it.unibo.dcs.service.authentication.server.handlers

import io.vertx.scala.ext.web.RoutingContext

/** It encapsulates the business logic of the service.
  * It handles all the incoming request to the service APIs. */
trait ServiceRequestHandler {

  /** User deletion request handler
    *
    * @param context Vertx routing context passed implicitly */
  def handleUserDeletion(implicit context: RoutingContext): Unit

  /** Token check request handler
    *
    * @param context Vertx routing context passed implicitly */
  def handleTokenCheck(implicit context: RoutingContext): Unit

  /** Registration request handler
    *
    * @param context Vertx routing context passed implicitly */
  def handleRegistration(implicit context: RoutingContext): Unit

  /** Login request handler
    *
    * @param context Vertx routing context passed implicitly */
  def handleLogin(implicit context: RoutingContext): Unit

  /** Logout request handler
    *
    * @param context Vertx routing context passed implicitly */
  def handleLogout(implicit context: RoutingContext): Unit
}
