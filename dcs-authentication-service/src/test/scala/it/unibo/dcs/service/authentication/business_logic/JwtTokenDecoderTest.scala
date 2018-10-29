package it.unibo.dcs.service.authentication.business_logic

import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class JwtTokenDecoderTest extends FlatSpec with MockFactory {

  private  val username = "ale"
  private  val token = "header.eyJzdWIiOiAiYWxlIn0=.signature"
  private  val expectedResult = true

  private  val tokenDecoder = JwtTokenDecoder()

  it should "return the correct user when the token gets decoded" in {
    assert(tokenDecoder getUsernameFromToken token equals username)
  }
}