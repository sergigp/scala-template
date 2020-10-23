package com.sergigp.template.acceptance.infrastructure.cleaner

import scala.concurrent.Future

trait Cleaner {
  def clean(): Future[Unit]
  def close(): Unit
}
