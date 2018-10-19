package it.unibo.dcs.authentication_service.business_logic

import io.vertx.lang.scala.json.Json
import org.apache.commons.codec.binary.Base64

/** Utility class useful to decode any jwt token, to get its payload */
class JwtTokenDecoder() {

  private val base64 = new Base64(true)

  /**
    * It decodes a token, obtaining the username present in the token's payload
    *
    * @param token the jwt token
    * @return a username */
  def getUsernameFromToken(token: String): String = {
    val base64EncodedBody = token.split("\\.")(1)
    val body = new String(base64.decode(base64EncodedBody))
    Json.fromObjectString(body).getString("sub")
  }

}

object JwtTokenDecoder{

  /** Factory method to instantiate the class
    *
    * @return an instantiation of the class */
  def apply(): JwtTokenDecoder = new JwtTokenDecoder()
}
