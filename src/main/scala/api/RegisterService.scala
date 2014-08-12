package api

import akka.actor.{ActorSystem, ActorRef}
import scala.concurrent.ExecutionContext
import spray.routing.Directives
import models.ndapidtos.{UserRegisterModel, DeviceRegisterModel}
import api.RegisterActor._
import models.{ErrorStatus, NDApiResponse}

/**
 * Created by nikolatonkev on 2014-05-20.
 */
class RegisterService(system: ActorSystem, registering: ActorRef)(implicit context: ExecutionContext)
  extends Directives with  DefaultJsonFormats  {

  implicit val DeviceRegisterFormater = jsonFormat2(DeviceRegisterModel)
  implicit val RegisterFormater = jsonFormat5(UserRegisterModel)
  implicit val NDResponseFormater = jsonFormat3(NDApiResponse[Boolean])
  implicit val NDRegisterDeviceResponseFormater = jsonFormat3(NDApiResponse[String])

  //http PUT http://localhost:8080/register < register.json
  //http PUT http://localhost:8080/registerdevice < registerdevice.json

  //Heroku
  //http PUT http://dry-atoll-6423.herokuapp.com/register < register.json
  val route =
    path("registeruser") {
      entity(as[UserRegisterModel]) { ent =>
        put {
          complete {
             new NDApiResponse[String](ErrorStatus.None, "", RegisterUser(system, ent))
          }
        }
      }
    }
    path("registerdevice") {
      entity(as[DeviceRegisterModel]) { ent =>
        put {
          complete {
            val authguid = RegisterDevice(system, ent)
            if(authguid !=  "") {
              new NDApiResponse[String](ErrorStatus.None, "", authguid.toString())
            }
            else {
              new NDApiResponse[String](ErrorStatus.None, "", "")
            }
          }
        }
      }
  }
}
