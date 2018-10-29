package it.unibo.dcs.service.authentication.business_logic

import java.util.Date

import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.service.authentication.server.setupHelpers.createJwtAuthOptions
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class JwtTokenGeneratorTest extends FlatSpec with MockFactory {

  private val username = "ale"
  private val tokenDecoder = JwtTokenDecoder()

  private val jwtAuth = JWTAuth.create(Vertx.vertx, createJwtAuthOptions())
  private val tokenGenerator = JwtTokenGenerator(jwtAuth)

  it should "return the correct user when the token gets decoded" in {
    val token = tokenGenerator generateToken username
    assert(tokenDecoder getUsernameFromToken token equals username)

    val issuedAtTimestamp = tokenDecoder getTokenIssuedAtTimestamp token
    val currentTimestamp = new Date().toInstant.toEpochMilli / 1000
    assert(issuedAtTimestamp > 0 && issuedAtTimestamp <= currentTimestamp)
  }
}