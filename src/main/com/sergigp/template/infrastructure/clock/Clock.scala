package com.sergigp.template.infrastructure.clock

import org.joda.time.DateTime

trait Clock {
  def now(): DateTime
}

object Clock {
  final class JodaClock extends Clock {
    override def now(): DateTime = DateTime.now()
  }
}
