package com.sergigp.template.behaviour.infrastructure

import scala.concurrent.Future

import com.sergigp.quasar.query.{Query, QueryBus}
import org.scalamock.scalatest.MockFactory

trait QueryBusMocked extends MockFactory {

  val queryBus: QueryBus[Future] = mock[QueryBus[Future]]

  def shouldAnswerSuccessfully[Q <: Query](query: Q, response: Q#QueryResponse): Unit =
    (queryBus.ask[Q] _)
      .expects(query)
      .once()
      .returns(Future.successful(response))

  def shouldAnswerUnsuccessfully[Q <: Query, E <: Throwable](query: Q, error: E): Unit =
    (queryBus.ask[Q] _)
      .expects(query)
      .once()
      .returns(Future.failed(error))
}
