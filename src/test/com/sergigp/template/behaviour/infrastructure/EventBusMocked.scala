package com.sergigp.template.behaviour.infrastructure

import scala.concurrent.Future

import com.sergigp.quasar.event.{Event, EventBus}
import org.scalamock.scalatest.MockFactory

trait EventBusMocked extends MockFactory {
  val eventBus: EventBus[Future] = mock[EventBus[Future]]

  def shouldPublishDomainEvents[DE <: Event](events: DE*): Unit = events.foreach(shouldPublishDomainEvent)

  def shouldPublishDomainEvent[DE <: Event](event: DE): Unit =
    (eventBus.publish[DE] _)
      .expects(event)
      .once()
      .returns(Future.successful(()))
}
