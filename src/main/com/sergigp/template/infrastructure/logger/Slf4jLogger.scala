package com.sergigp.template.infrastructure.logger

import org.apache.logging.log4j.{Level, LogManager, ThreadContext}
import org.apache.logging.log4j

final class Slf4jLogger extends Logger {

  Level.forName("CRITICAL", 101)

  private val criticalLogLevel = Level.getLevel("CRITICAL")
  val logger: log4j.Logger     = LogManager.getLogger()

  def info(message: => String, context: (String, Any)*): Unit = withContext(context)(logger.info(message))

  def info(
    error: => Throwable,
    message: => String,
    context: (String, Any)*
  ): Unit = withThrowableParam(error, context) { params =>
    withContext(params) {
      logger.info(message, error)
    }
  }

  def warning(message: => String, context: (String, Any)*): Unit = withContext(context)(logger.warn(message))

  def warning(
    error: => Throwable,
    message: => String,
    context: (String, Any)*
  ): Unit = withThrowableParam(error, context) { params =>
    withContext(params) {
      logger.warn(message, error)
    }
  }

  def critical(message: => String, context: (String, Any)*): Unit =
    withContext(context)(logger.log(criticalLogLevel, message))

  def critical(error: => Throwable, message: => String, context: (String, Any)*): Unit =
    withThrowableParam(error, context) { params =>
      withContext(params) {
        logger.log(criticalLogLevel, message, error)
      }
    }

  private def withContext(params: Seq[(String, Any)])(logAction: => Unit): Unit = {
    params.foreach { case (name, value) => ThreadContext.put(name, value.toString) }
    logAction
    ThreadContext.clearAll()
  }

  private def withThrowableParam(t: Throwable, params: Seq[(String, Any)])(
    logAction: Seq[(String, Any)] => Unit
  ): Unit = logAction(("exception_class" -> t.getClass.getName) +: params)
}
