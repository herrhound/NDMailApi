package web

import akka.io.IO
import spray.can.Http
import api.Api
import core.{CoreActors, Core}


trait Web {
  this: Api with CoreActors with Core =>

  val host = system.settings.config.getString("NDMailApi.interface")
  val port = system.settings.config.getInt("NDMailApi.port")

  IO(Http) ! Http.Bind(rootService, host, port)
}
