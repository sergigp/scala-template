package com.sergigp.template.infrastructure

import play.api.libs.ws.ahc.StandaloneAhcWSClient

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.sergigp.template.infrastructure.config.AppConfig
import com.sergigp.template.infrastructure.logger.Logger

class InfrastructureContext(
  val config: AppConfig,
  logger: Logger
)(
  implicit system: ActorSystem,
  materializer: Materializer
) {
  // create your persistence connections (and other infrastructure) here

  private val httpClient = StandaloneAhcWSClient()

  def close(): Unit =
    httpClient.close()
}
