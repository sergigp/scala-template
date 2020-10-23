package com.sergigp.template.acceptance.infrastructure.cleaner

case class CleanResources(
  // add your cleaners here like:
  // mysql: Boolean = false,
) {
  def needToClean(resource: String): Boolean = resource match {
    case other => throw new RuntimeException(s"requiring to clean a non known resource $other")
  }
}
