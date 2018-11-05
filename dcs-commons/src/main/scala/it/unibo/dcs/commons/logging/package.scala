package it.unibo.dcs.commons

import ch.qos.logback.classic.{Level, Logger => LogbackLogger}
import io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME
import io.vertx.core.logging.{SLF4JLogDelegateFactory, LoggerFactory => VertxLoggerFactory}
import io.vertx.lang.scala.ScalaLogger
import org.slf4j.{Logger => SLF4JLogger, LoggerFactory => SLF4JLoggerFactory}

package object logging {

  trait Logging {

    // initializeLogging()

    protected[this] lazy val log: ScalaLogger = ScalaLogger.getLogger(getClass.getName)

  }

  def initializeLogging(): Unit = {
    if (!initialized) {
      System.setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, classOf[SLF4JLogDelegateFactory].getName)
      VertxLoggerFactory.getLogger(classOf[VertxLoggerFactory])
      initialized = true
    }
  }

  def setLevel(level: Level): Unit = root.setLevel(level)

  private var initialized = false

  private lazy val root = SLF4JLoggerFactory.getLogger(SLF4JLogger.ROOT_LOGGER_NAME).asInstanceOf[LogbackLogger]

}
