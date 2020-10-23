package com.sergigp.template.infrastructure.controller

import scala.concurrent.ExecutionContext

import play.api.libs.json.{JsObject, JsString}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.server.Directives._

final class ReadinessController(implicit ec: ExecutionContext) extends Controller {

  override protected val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case _ =>
      complete(StatusCodes.InternalServerError)
  }

  val route: Route = get {
    path("ready") {
      handleErrors(exceptionHandler) {
        // put your readiness logic here
        complete(JsObject(Map("ready" -> JsString("ok"))))
      }
    }
  }
}
