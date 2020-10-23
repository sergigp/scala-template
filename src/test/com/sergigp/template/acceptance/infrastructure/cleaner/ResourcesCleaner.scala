package com.sergigp.template.acceptance.infrastructure.cleaner

import scala.concurrent.{ExecutionContext, Future}

import cats.implicits._
import com.sergigp.template.infrastructure.InfrastructureContext
import com.sergigp.template.infrastructure.config.AppConfig
import com.typesafe.config.Config

class ResourcesCleaner(
  infrastructureContext: InfrastructureContext,
  config: AppConfig,
  testConfig: Config
)(implicit ec: ExecutionContext) {

  private val cleaners: Map[String, Cleaner] = Map(
    // instantiate your cleaners here
  )

  def clean(cleanResources: CleanResources): Future[Unit] = {
    val resourcesToClean = cleaners
      .filter {
        case (resource, _) => cleanResources.needToClean(resource)
      }
      .values
      .toList

    // @TODO improve paralellization
    resourcesToClean.traverse(_.clean()).map(_ => ())
  }

  def close(): Unit = cleaners.values.foreach(_.close())
}
