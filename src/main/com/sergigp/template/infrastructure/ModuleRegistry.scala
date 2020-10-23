package com.sergigp.template.infrastructure

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.sergigp.quasar.command.CommandBus
import com.sergigp.quasar.event.EventBus
import com.sergigp.quasar.query.QueryBus
import com.sergigp.template.infrastructure.clock.Clock
import com.sergigp.template.infrastructure.config.AppConfig
import com.sergigp.template.infrastructure.logger.Logger
import com.sergigp.template.infrastructure.module.StatefulModuleContext
import com.sergigp.template.infrastructure.uuid.UuidProvider

final class ModuleRegistry(
  config: AppConfig,
  infrastructureContext: InfrastructureContext,
  actorSystem: ActorSystem,
  commandBus: CommandBus[Future],
  eventBus: EventBus[Future],
  queryBus: QueryBus[Future],
  clock: Clock,
  javaUuidProvider: UuidProvider,
  logger: Logger
)(implicit ec: ExecutionContext, materializer: Materializer) {

  // start your modules here
  val statefulModules: Seq[StatefulModuleContext] = List.empty

  def init(): Unit = statefulModules.foreach(_.init())

  def terminate(): Future[Unit] =
    Future
      .sequence(
        statefulModules.map(_.terminate()) :+ actorSystem.terminate().map(_ => ())
      )
      .map(_ => ())
}
