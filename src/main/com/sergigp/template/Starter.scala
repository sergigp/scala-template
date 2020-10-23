package com.sergigp.template

import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import com.sergigp.template.infrastructure.{AppContext, ExecutionContextContainer}
import com.sergigp.template.infrastructure.config.AppConfig
import com.sergigp.template.infrastructure.logger.Slf4jLogger
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Starter {
  def main(args: Array[String]): Unit = {
    val config = Option(System.getProperty("config.resource"))
      .map(path => ConfigFactory.load(path))
      .getOrElse(ConfigFactory.load("application.conf"))
      .resolve()

    val appConfig = AppConfig(config)

    val logger = new Slf4jLogger()
    logger.warning(s"starting ${appConfig.appName} application")

    val executionContextContainer = ExecutionContextContainer(
      ExecutionContext.Implicits.global
    )

    val actorSystem: ActorSystem   = ActorSystem()
    val materializer: Materializer = SystemMaterializer(actorSystem).materializer

    new AppContext(
      appConfig,
      logger,
      actorSystem,
      materializer,
      executionContextContainer
    ).init()
  }
}
