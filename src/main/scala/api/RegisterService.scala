package api

import akka.actor.{ActorSystem, ActorRef}
import scala.concurrent.ExecutionContext
import spray.routing.Directives
import models.ndapidtos.{RegisterModel, DeviceRegisterModel}
import api.RegisterActor._
import models.{ErrorStatus, NDApiResponse}

/**
 * Created by nikolatonkev on 2014-05-20.
 */
class RegisterService(system: ActorSystem, registering: ActorRef)(implicit context: ExecutionContext)
  extends Directives with  DefaultJsonFormats  {

  implicit val DeviceRegisterFormater = jsonFormat2(DeviceRegisterModel)
  implicit val RegisterFormater = jsonFormat5(RegisterModel)
  implicit val NDResponseFormater = jsonFormat3(NDApiResponse[Boolean])

  //http PUT http://localhost:8080/register < register.json
  //http PUT http://localhost:8080/registerdevice < registerdevice.json
  val route =
    path("register") {
      entity(as[RegisterModel]) { ent =>
        put {
          complete {
            val data = Register(ent)
            val response = new NDApiResponse[Boolean](ErrorStatus.None, "", data)
            response
            //ent
          }
        }
      }
    }~
    path("registerdevice") {
      entity(as[DeviceRegisterModel]) { ent =>
        put {
          complete {
            val data = RegisterDevice(system, ent)
            val response = new NDApiResponse[Boolean](ErrorStatus.None, "", data)
            response
            //ent
          }
        }
      }
  }
}
