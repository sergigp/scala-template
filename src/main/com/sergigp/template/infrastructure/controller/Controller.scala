package com.sergigp.template.infrastructure.controller

import play.api.libs.json.{JsArray, JsObject, JsString}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

abstract class Controller() extends PlayJsonSupport {

  def route: Route
  protected val exceptionHandler: ExceptionHandler

  def handleErrors(exceptionHandler: ExceptionHandler): Directive[Unit] =
    handleExceptions(exceptionHandler) & handleRejections(rejectionsHandler)

  private val rejectionsHandler: RejectionHandler = RejectionHandler
    .newBuilder()
    .handle {
      case ex: ValidationRejection =>
        val errorMsg = "validation rejection has occurred: " + ex.message
        complete(StatusCodes.BadRequest, JsObject(Map("errors" -> JsArray(Seq(JsString(errorMsg))))))
      case ex: MalformedRequestContentRejection =>
        val errorMsg = "malformed request has occurred: " + ex.message
        complete(StatusCodes.BadRequest, JsObject(Map("errors" -> JsArray(Seq(JsString(errorMsg))))))
    }
    .result()
}
