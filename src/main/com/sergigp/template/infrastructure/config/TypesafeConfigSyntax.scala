package com.sergigp.template.infrastructure.config

import scala.concurrent.duration.{Duration, FiniteDuration}

import com.typesafe.config.Config

object TypesafeConfigSyntax {

  implicit class RichTypesafeConfig(config: Config) {
    def isNull(path: String): Boolean = config.getIsNull(path)

    def bool(path: String): Boolean = config.getBoolean(path)

    def int(path: String): Int = config.getInt(path)

    def double(path: String): Double = config.getDouble(path)

    def string(path: String): String = config.getString(path)

    def finiteDuration(path: String): FiniteDuration = Duration.fromNanos(config.getDuration(path).toNanos)

    def optionalString(path: String): Option[String] = if (isNull(path)) None else Some(string(path))
  }
}
