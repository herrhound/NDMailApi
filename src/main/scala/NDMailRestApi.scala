import akka.actor.{ActorSystem}
import api.Api
import core.{Core, CoreActors}
import web.Web

/**
 * Created by Nikola on 3/14/14.
 */
object NDMailRestApi extends App with Core with CoreActors with Api with Web {

  implicit lazy val system = ActorSystem("NDMailApi")

  //val logger = LoggerFactory.getLogger(this.getClass)
  //logger.debug("Hit any key to stop the service.")
  println("Hit any key to stop the service!!!")
  val result = readLine()
  system.shutdown()
}
//with Boot //with CoreActors