package com.sergigp.template.behaviour.infrastructure

import scala.concurrent.Future

import com.sergigp.quasar.command.{Command, CommandBus}
import org.scalamock.scalatest.MockFactory

trait CommandBusMocked extends MockFactory {
  val commandBus: CommandBus[Future] = mock[CommandBus[Future]]

  def shouldPublishSuccessfully[C <: Command](command: C, response: C#CommandReturnType): Unit =
    (commandBus.publish[C] _)
      .expects(command)
      .once()
      .returns(Future.successful(response))

  def shouldPublishUnsuccessfully[C <: Command, E <: Throwable](command: C, error: E): Unit =
    (commandBus.publish[C] _)
      .expects(command)
      .once()
      .returns(Future.failed(error))
}
