package com.sergigp.template.acceptance

import akka.http.scaladsl.model.StatusCodes
import com.sergigp.template.acceptance.infrastructure.cleaner.CleanResources

final class ReadinessControllerTest extends AcceptanceTest {
  override val cleanResources: CleanResources = CleanResources()

  "App" should {
    "return ok when started and queried to readiness endpoint" in statelessRunner { c =>
      c.getting("/ready") {
        status shouldBe StatusCodes.OK
      }
    }
  }
}
