package it.unibo.dcs.authentication_service.business_logic

import java.util.Date

import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.auth.jwt.{JWTAuth, JWTOptions}

/** Utility class useful to generate jwt tokens */
class JwtTokenGenerator(jwtAuth: JWTAuth) {

  /**
    * It generates a token, given a username
    *
    * @param username the username of the user
    * @return a jwt token */
  def generateToken(username: String): String = {
    jwtAuth.generateToken(Json.obj(("sub", username), ("iat", new Date().toInstant.toEpochMilli / 1000)),
      JWTOptions().setExpiresInMinutes(1440)) //expires after one day
  }

}

object JwtTokenGenerator{

  /** Factory method to instantiate the class
    *
    * @param jwtAuth jwt authentication provider
    * @return an instantiation of the class */
  def apply(jwtAuth: JWTAuth): JwtTokenGenerator = new JwtTokenGenerator(jwtAuth)
}
