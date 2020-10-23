package com.sergigp.template.infrastructure.config

import com.typesafe.config.Config

object AppConfig {
  def apply(config: Config): AppConfig = AppConfig(
    config.getString("app-name")
  )
}

final case class AppConfig(
  appName: String
)
