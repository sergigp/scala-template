package com.sergigp.template.acceptance.infrastructure.clock

import com.sergigp.template.infrastructure.clock.Clock
import org.joda.time.DateTime

object ConstantClock extends Clock {
  val fixedDate: DateTime = DateTime.now()

  override def now(): DateTime = fixedDate
}
