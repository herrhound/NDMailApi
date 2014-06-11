package web

import akka.io.IO
import spray.can.Http
import api.Api
import core.{CoreActors, Core}
import util.Properties

trait Web {
  this: Api with CoreActors with Core =>

  val host = system.settings.config.getString("NDMailApi.interface")
  val _port = Properties.envOrElse("PORT", "8080").toInt
  println(_port)
  IO(Http) ! Http.Bind(rootService, interface = "0.0.0.0", port = _port)
}
