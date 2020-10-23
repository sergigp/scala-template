package com.sergigp.template.integration

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.sergigp.template.infrastructure.config.AppConfig
import com.sergigp.template.infrastructure.logger.{Logger, NoopLogger}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Span}

abstract class IntegrationTest
    extends WordSpecLike
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest
    with BeforeAndAfterAll
    with Eventually {

  val config                     = AppConfig(ConfigFactory.load)
  val logger: Logger             = NoopLogger
  implicit val p: PatienceConfig = PatienceConfig(Span(20000, Millis), Span(100, Millis))

  // create your runners here for testing your infrastructure implementations like:
  // def mysqlRunner(test: DbConnection => Unit): Unit
  // remember to clean infra on every test !!
}
