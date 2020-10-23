package com.sergigp.template.infrastructure.logger

object NoopLogger extends Logger {

  def info(message: => String, context: (String, Any)*): Unit = ()

  def info(error: => Throwable, message: => String, context: (String, Any)*): Unit = ()

  def warning(message: => String, context: (String, Any)*): Unit = ()

  def warning(error: => Throwable, message: => String, context: (String, Any)*): Unit = ()

  def critical(message: => String, context: (String, Any)*): Unit = ()

  def critical(error: => Throwable, message: => String, context: (String, Any)*): Unit = ()
}
