package it.unibo.dcs.commons.dataaccess

import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.sql.ResultSet

import scala.collection.mutable

object ResultSetHelper {

  object Implicits {

    implicit class RichResultSet(resultSet: ResultSet) {

      def getRows: mutable.Buffer[JsonObject] =
        resultSet.getResults
          .map(result => Stream.range(0, result.size)
            .map(i => (resultSet.getColumnNames(i), result.getValue(i)))
            .foldLeft(new JsonObject()) {
              (result, value) => result.put(value._1, value._2)
            })

    }

  }

}
