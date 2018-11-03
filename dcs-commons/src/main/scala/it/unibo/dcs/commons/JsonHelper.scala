package it.unibo.dcs.commons

import com.google.gson.Gson
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import it.unibo.dcs.commons.ReflectionHelper.asClassOf

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.reflect.ClassTag

object JsonHelper {

  object Implicits {

    implicit def jsonArrayToString(json: JsonArray): String = json.encode()

    implicit def jsonObjectToString(json: JsonObject): String = json.encode()

    implicit class RichGson(gson: Gson) {

      def toJsonArray(src: Iterable[_]): JsonArray = Json.fromArrayString(gson toJsonString src)

      def toJsonObject(src: AnyRef): JsonObject = Json.fromObjectString(gson toJson src)

      def toJsonString(src: Iterable[_]): String = gson toJson src.asJava

      def fromJsonArray[T](json: JsonArray)(implicit ct: ClassTag[Array[T]]): Seq[T] =
        gson.fromJsonString[Array[T]](json.encode())

      def fromJsonObject[T](json: JsonObject)(implicit ct: ClassTag[T]): T =
        gson.fromJsonString[T](json.encode())

      def fromJsonString[T](json: String)(implicit ct: ClassTag[T]): T =
        gson.fromJson[T](json, asClassOf(ct))

    }

  }

}
