package com.sergigp.template.infrastructure.uuid

import java.util.UUID

class UuidProvider {
  def next(): UUID = UUID.randomUUID()
}
