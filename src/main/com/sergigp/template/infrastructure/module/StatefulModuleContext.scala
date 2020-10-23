package com.sergigp.template.infrastructure.module

import scala.concurrent.Future

trait StatefulModuleContext {
  def init(): Unit

  def terminate(): Future[Unit]
}
