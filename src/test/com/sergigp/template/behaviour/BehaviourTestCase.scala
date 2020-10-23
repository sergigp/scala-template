package com.sergigp.template.behaviour

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

import akka.util.Timeout
import com.sergigp.quasar.command.Command
import com.sergigp.quasar.event.Event
import com.sergigp.quasar.query.Query
import com.sergigp.template.behaviour.infrastructure.{CommandBusMocked, EventBusMocked, QueryBusMocked}
import com.sergigp.template.infrastructure.clock.Clock
import com.sergigp.template.infrastructure.logger.{Logger, NoopLogger}
import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import org.scalatest.concurrent.{Eventually, ScalaFutures}

abstract class BehaviourTestCase
    extends WordSpecLike
    with Matchers
    with OptionValues
    with ScalaFutures
    with Eventually
    with GivenWhenThen
    with EitherValues
    with OneInstancePerTest
    with MockFactory
    with QueryBusMocked
    with CommandBusMocked
    with EventBusMocked
    with ParallelTestExecution {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val timeout: Timeout             = 5 seconds

  val logger: Logger         = NoopLogger
  protected val clock: Clock = mock[Clock]

  protected val commandSuccess                           = Right(())
  protected def commandFailure[E <: Throwable](error: E) = Left(error)

  protected def querySuccess(response: Any)            = Right(response)
  protected def queryFailure[E <: Throwable](error: E) = Left(error)

  def shouldReturnNow(date: DateTime): Unit =
    (clock.now _)
      .expects()
      .once()
      .returns(date)

  def shouldHandleEventSuccessfully[E <: Event](event: E, handler: E => Future[Unit]): Unit =
    handler(event).futureValue should be(())

  def shouldHandleCommandSuccessfully[C <: Command](command: C, handler: C => Future[C#CommandReturnType]): Unit =
    handler(command).futureValue should be(Right())

  def shouldHandleCommandFailure[C <: Command, E <: Throwable](
    command: C,
    handler: C => Future[C#CommandReturnType],
    error: E
  ): Unit =
    handler(command).futureValue should be(
      commandFailure(error)
    )

  protected def shouldCrashHandlingEvent(handler: Future[Unit], error: Throwable): Unit =
    ScalaFutures.whenReady(handler.failed) { e =>
      e should ===(error)
    }

  def shouldHandleQuerySuccessfully[Q <: Query](
    query: Q,
    handler: Q => Future[Q#QueryResponse],
    response: Q#QueryResponse
  ): Unit =
    handler(query).futureValue should be(response)

  def shouldFailAnsweringQuery[Q <: Query, E <: Throwable](
    query: Q,
    handler: Q => Future[Q#QueryResponse],
    error: E
  ): Unit = handler(query).futureValue should be(Left(error))
}
