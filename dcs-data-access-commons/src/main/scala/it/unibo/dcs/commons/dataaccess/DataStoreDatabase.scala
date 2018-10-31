package it.unibo.dcs.commons.dataaccess

import io.vertx.lang.scala.json.JsonArray
import io.vertx.scala.ext.sql.{ResultSet, SQLConnection, UpdateResult}
import it.unibo.dcs.commons.RxHelper.Implicits.RichObservable
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.VertxHelper.Implicits.functionToHandler
import rx.lang.scala.Observable

abstract class DataStoreDatabase(private[this] val connection: SQLConnection) {

  def execute(sql: String): Observable[Unit] = VertxHelper.toObservable[Unit](connection.execute(sql, _))

  def execute(sql: String, params: JsonArray): Observable[Unit] = update(sql, params).toCompletable

  def query(sql: String): Observable[ResultSet] = VertxHelper.toObservable[ResultSet](connection.query(sql, _))

  def query(sql: String, params: JsonArray): Observable[ResultSet] =
    VertxHelper.toObservable[ResultSet](connection.queryWithParams(sql, params, _))

  def update(sql: String): Observable[UpdateResult] =
    VertxHelper.toObservable[UpdateResult](connection.update(sql, _))

  def update(sql: String, params: JsonArray): Observable[UpdateResult] =
    VertxHelper.toObservable[UpdateResult](connection.updateWithParams(sql, params, _))

}
