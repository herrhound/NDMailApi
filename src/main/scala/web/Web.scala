package web

import akka.io.IO
import spray.can.Http
import api.Api
import core.{CoreActors, Core}
import util.Properties

trait Web {
  this: Api with CoreActors with Core =>

  val host = system.settings.config.getString("NDMailApi.interface")
  val alternativePort = system.settings.config.getInt("NDMailApi.port")
  //val port = Option(System.getenv("PORT")).getOrElse(alternativePort).toString.toInt
  //val port = scala.util.Properties.envOrElse("PORT", "80").toInt

  val port = Properties.envOrElse("PORT", "8080").toInt

  IO(Http) ! Http.Bind(rootService, host, port)
}
