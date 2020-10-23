package com.sergigp.template.acceptance

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Try}

import play.api.libs.json.Writes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.stream.SystemMaterializer
import akka.util.Timeout
import com.sergigp.quasar.command.AsyncCommandBus
import com.sergigp.quasar.event.AsyncEventBus
import com.sergigp.quasar.query.AsyncQueryBus
import com.sergigp.template.acceptance.infrastructure.cleaner.{CleanResources, ResourcesCleaner}
import com.sergigp.template.acceptance.infrastructure.clock.ConstantClock
import com.sergigp.template.infrastructure.{ExecutionContextContainer, InfrastructureContext, ModuleRegistry, Router}
import com.sergigp.template.infrastructure.config.AppConfig
import com.sergigp.template.infrastructure.logger.{Logger, NoopLogger}
import com.sergigp.template.infrastructure.uuid.UuidProvider
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, RandomTestOrder, WordSpecLike}
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Span}
import org.slf4j.LoggerFactory

abstract class AcceptanceTest
    extends WordSpecLike
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest
    with BeforeAndAfterAll
    with Eventually
    with RandomTestOrder {

  protected val cleanResources: CleanResources
  implicit val timeout: Timeout  = Timeout(5.seconds)
  implicit val p: PatienceConfig = PatienceConfig(Span(10000, Millis), Span(100, Millis))
  val config: AppConfig          = AppConfig(ConfigFactory.load)
  val logger: Logger             = NoopLogger
  val uuidProvider               = new UuidProvider()
  val clock                      = ConstantClock

  val executionContextContainer = ExecutionContextContainer(
    ExecutionContext.Implicits.global
  )

  def statefulRunner(test: StatefullAcceptanceContext => Unit): Unit = {
    val actorSystem  = ActorSystem("stateful_acceptance")
    val materializer = SystemMaterializer(actorSystem).materializer
    val infrastructureContext = new InfrastructureContext(
      config,
      logger
    )(actorSystem, materializer)
    val resourcesCleaner = new ResourcesCleaner(infrastructureContext, config, testConfig)
    val context          = new StatefullAcceptanceContext(actorSystem, infrastructureContext)

    def closeResources(): Future[Unit] = {
      infrastructureContext.close()
      resourcesCleaner.close()
      Future.unit
    }

    run(
      _ => resourcesCleaner.clean(cleanResources),
      test,
      context,
      _ => closeResources()
    )
  }

  def statelessRunner(test: StatelessAcceptanceContext => Unit): Unit = {
    val actorSystem  = ActorSystem("stateful_acceptance")
    val materializer = SystemMaterializer(actorSystem).materializer
    val infrastructureContext = new InfrastructureContext(
      config,
      logger
    )(actorSystem, materializer)
    val resourcesCleaner = new ResourcesCleaner(infrastructureContext, config, testConfig)

    def closeResources(): Future[Unit] = {
      infrastructureContext.close()
      resourcesCleaner.close()
      actorSystem.terminate().map(_ => ())
    }

    val context = new StatelessAcceptanceContext(system, infrastructureContext)

    run(
      _ => resourcesCleaner.clean(cleanResources),
      test,
      context,
      _ => closeResources()
    )
  }

  private def run[C <: AcceptanceContext](
    beforeTest: Unit => Future[Unit],
    test: C => Unit,
    context: C,
    afterTest: Unit => Future[Unit]
  ) = {
    beforeTest().futureValue
    Try(test(context)) match {
      case Failure(e) =>
        context.moduleRegistry.terminate().futureValue
        afterTest().futureValue
        throw new Exception(s"acceptance test failed: ${e.getMessage}", e)
      case _ =>
        context.moduleRegistry.terminate().futureValue
        afterTest().futureValue
    }
  }

  abstract class AcceptanceContext(
    actorSystem: ActorSystem,
    infrastructureContext: InfrastructureContext
  ) {
    val commandBus = new AsyncCommandBus(LoggerFactory.getLogger(config.appName))
    val eventBus   = new AsyncEventBus()
    val queryBus   = new AsyncQueryBus(LoggerFactory.getLogger(config.appName))

    val moduleRegistry = new ModuleRegistry(
      config,
      infrastructureContext,
      actorSystem,
      commandBus,
      eventBus,
      queryBus,
      clock,
      uuidProvider,
      logger
    )
    val router = new Router(
      actorSystem,
      commandBus,
      queryBus,
      config,
      logger
    )

    implicit val timeout = RouteTestTimeout(10.seconds)

    def getting[T](path: String)(body: => T): T = Get(path) ~> router.all ~> check(body)
    def posting[T](path: String)(body: => T): T = Post(path) ~> router.all ~> check(body)
    def postingWithPayload[T, P: Writes](path: String, payload: P)(body: => T): T =
      Post(path).withEntity(
        HttpEntity(ContentTypes.`application/json`, implicitly[Writes[P]].writes(payload).toString())
      ) ~> router.all ~> check(body)
    def deleting[T](path: String)(body: => T): T = Delete(path) ~> router.all ~> check(body)
    def deletingWithPayload[T, P: Writes](path: String, payload: P)(body: => T): T =
      Delete(path).withEntity(
        HttpEntity(ContentTypes.`application/json`, implicitly[Writes[P]].writes(payload).toString())
      ) ~> router.all ~> check(body)
    def puttingWithPayload[T, P: Writes](path: String, payload: P)(body: => T): T =
      Put(path).withEntity(
        HttpEntity(ContentTypes.`application/json`, implicitly[Writes[P]].writes(payload).toString())
      ) ~> router.all ~> check(body)
    def patching[T](path: String)(body: => T): T = Patch(path) ~> router.all ~> check(body)
    def patchingWithPayload[T, P: Writes](path: String, payload: P)(body: => T): T =
      Patch(path).withEntity(
        HttpEntity(ContentTypes.`application/json`, implicitly[Writes[P]].writes(payload).toString())
      ) ~> router.all ~> check(body)
    def postingWithStringPayload[T](path: String, payload: String)(body: => T): T =
      Post(path).withEntity(
        HttpEntity(ContentTypes.`application/json`, payload)
      ) ~> router.all ~> check(body)
    def puttingWithStringPayload[T](path: String, payload: String)(body: => T): T =
      Put(path).withEntity(
        HttpEntity(ContentTypes.`application/json`, payload)
      ) ~> router.all ~> check(body)
    def patchingWithStringPayload[T](path: String, payload: String)(body: => T): T =
      Patch(path).withEntity(
        HttpEntity(ContentTypes.`application/json`, payload)
      ) ~> router.all ~> check(body)
  }

  final class StatefullAcceptanceContext(
    actorSystem: ActorSystem,
    infrastructureContext: InfrastructureContext
  ) extends AcceptanceContext(actorSystem, infrastructureContext) {
    // shortcut your repositories and other infrastructure from infrastructureContext
    moduleRegistry.init()
  }

  final class StatelessAcceptanceContext(
    actorSystem: ActorSystem,
    infrastructureContext: InfrastructureContext
  ) extends AcceptanceContext(actorSystem, infrastructureContext) {
    // shortcut your repositories and other infrastructure from infrastructureContext
  }
}
