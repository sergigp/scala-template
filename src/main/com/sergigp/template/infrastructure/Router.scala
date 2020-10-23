package com.sergigp.template.infrastructure

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.sergigp.quasar.command.CommandBus
import com.sergigp.quasar.query.QueryBus
import com.sergigp.template.infrastructure.config.AppConfig
import com.sergigp.template.infrastructure.controller.{ReadinessController, StatusController}
import com.sergigp.template.infrastructure.logger.Logger

final class Router(
  actorSystem: ActorSystem,
  commandBus: CommandBus[Future],
  queryBus: QueryBus[Future],
  config: AppConfig,
  logger: Logger
)(implicit ec: ExecutionContext) {
  val corsSettings: CorsSettings =
    CorsSettings.defaultSettings.withAllowedMethods(Seq(GET, POST, PATCH, PUT, DELETE, HEAD, OPTIONS))

  val all: Route = cors(corsSettings)(
    statusController.route ~
    readinessController.route
  )

  private def statusController    = new StatusController()
  private def readinessController = new ReadinessController()
}
