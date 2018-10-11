package it.unibo.dcs.commons

import java.nio.charset.{Charset, StandardCharsets}

import io.vertx.core.json.JsonObject
import org.apache.commons.io.IOUtils

object IoHelper {

  def readJsonObject(file: String, enc: Charset = StandardCharsets.UTF_8): JsonObject = {
    new JsonObject(readString(file, enc))
  }

  def readString(file: String, enc: Charset = StandardCharsets.UTF_8): String = {
    val in = getClass.getResourceAsStream(file)
    IOUtils.toString(in, enc)
  }

}
