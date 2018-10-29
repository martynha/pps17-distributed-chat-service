package it.unibo.dcs.commons

import com.google.gson.Gson
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import it.unibo.dcs.commons.ReflectionHelper.asClassOf

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

object JsonHelper {

  def toJsonArray(gson: Gson, src: Iterable[_]): JsonArray = Json.fromArrayString(toJsonString(gson, src))

  def toJsonObject(gson: Gson, src: AnyRef): JsonObject = Json.fromObjectString(gson.toJson(src))

  def toJsonString(gson: Gson, src: Iterable[_]): String = gson.toJson(src.asJava)

  def fromJson[T](gson: Gson, json: JsonArray)(implicit ct: ClassTag[Array[T]]): List[T] =
    gson.fromJson(json.encode(), asClassOf(ct)).toList

  def fromJson[T](gson: Gson, json: JsonObject)(implicit ct: ClassTag[T]): T =
    gson.fromJson[T](json.encode(), asClassOf(ct))

  object Implicits {

    implicit def jsonArrayToString(json: JsonArray): String = json.encode()

    implicit def jsonObjectToString(json: JsonObject): String = json.encode()

  }

}
