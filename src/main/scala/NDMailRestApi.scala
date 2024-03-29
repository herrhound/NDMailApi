import akka.actor.{ActorSystem}
import api.Api
import core.{Core, CoreActors}

import org.slf4j.LoggerFactory
import web.Web

/**
 * Created by Nikola on 3/14/14.
 */


object NDMailRestApi extends App with Core with CoreActors with Api with Web {

  implicit lazy val system = ActorSystem("NDMailApi")
  val logger = LoggerFactory.getLogger(this.getClass)
  logger.debug("Server running")
  println("Server running")
  //val result = readLine()
  //system.shutdown()
}
