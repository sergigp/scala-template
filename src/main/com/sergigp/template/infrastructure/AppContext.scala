package com.sergigp.template.infrastructure

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import com.sergigp.quasar.command.AsyncCommandBus
import com.sergigp.quasar.event.AsyncEventBus
import com.sergigp.quasar.query.AsyncQueryBus
import com.sergigp.template.infrastructure.clock.Clock.JodaClock
import com.sergigp.template.infrastructure.config.AppConfig
import com.sergigp.template.infrastructure.logger.Slf4jLogger
import com.sergigp.template.infrastructure.uuid.UuidProvider
import org.slf4j.LoggerFactory

final class AppContext(
  config: AppConfig,
  logger: Slf4jLogger,
  actorSystem: ActorSystem,
  appMaterializer: Materializer,
  executionContextContainer: ExecutionContextContainer
) {
  implicit val system: ActorSystem        = actorSystem
  implicit val materializer: Materializer = appMaterializer

  implicit private val globalExecutionContext: ExecutionContext = executionContextContainer.global

  private val commandBus = new AsyncCommandBus(LoggerFactory.getLogger(config.appName))

  private val eventBus = new AsyncEventBus()

  private val queryBus = new AsyncQueryBus(LoggerFactory.getLogger(config.appName))

  private val infrastructureContext = new InfrastructureContext(config, logger)

  private val modules = new ModuleRegistry(
    config,
    infrastructureContext,
    actorSystem,
    commandBus,
    eventBus,
    queryBus,
    new JodaClock,
    new UuidProvider,
    logger
  )

  private val router = new Router(actorSystem, commandBus, queryBus, config, logger)

  def init(): Unit = {
    modules.init()
    Await.result(Http().bindAndHandle(router.all, "0.0.0.0", 9000), Duration.Inf)
  }
}
