package com.sergigp.template.infrastructure.controller

import play.api.libs.json.{JsObject, JsString}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.server.Directives._

final class StatusController extends Controller {

  override protected val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case _ =>
      complete(StatusCodes.InternalServerError)
  }

  val route: Route = get {
    path("status") {
      handleErrors(exceptionHandler) {
        complete(JsObject(Map("status" -> JsString("ok"))))
      }
    }
  }
}
