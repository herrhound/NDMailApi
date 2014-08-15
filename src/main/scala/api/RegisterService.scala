package api

import akka.actor.{Props, ActorSystem, ActorRef}
import scala.concurrent.{Future, ExecutionContext}
import spray.routing.Directives


import models.ndapidtos._
import api.RegisterActor._
import models.{GoogleToken, OAuth2CallbackModel, ErrorStatus, NDApiResponse}
import spray.httpx.encoding.{Gzip, Deflate}

import models.GoogleToken
import models.ndapidtos.UserRegisterDTO
import models.ndapidtos.DeviceRegisterModel
import models.NDApiResponse
import models.OAuth2CallbackModel
import spray.http.HttpRequest
import models.GoogleToken
import models.ndapidtos.UserRegisterDTO
import models.ndapidtos.DeviceRegisterModel
import models.NDApiResponse
import models.OAuth2CallbackModel
import spray.http.HttpRequest
import models.ndapidtos.UserRegisterDTO
import models.NDApiResponse
import spray.http.HttpResponse
import models.GoogleToken
import models.ndapidtos.DeviceRegisterModel
import models.OAuth2CallbackModel

// HTTP related imports
import spray.http._
import spray.client.pipelining._

import spray.http.HttpHeaders.Accept
import shapeless.~>
import spray.util._

// Futures related imports
import scala.concurrent.Future
import scala.util.{ Success, Failure }

import spray.httpx.SprayJsonSupport._
import spray.client._

/**
 * Created by nikolatonkev on 2014-05-20.
 */

/*
trait WebClient {
  def post(url: String): Future[GoogleToken]
}
*/

class RegisterService(system: ActorSystem, registering: ActorRef)(implicit context: ExecutionContext)
  extends Directives with  DefaultJsonFormats  {

  import ExecutionContext.Implicits.global

  implicit val DeviceRegisterFormater = jsonFormat2(DeviceRegisterModel)
  implicit val RegisterFormater = jsonFormat12(UserRegisterDTO)
  implicit val NDResponseFormater = jsonFormat3(NDApiResponse[Boolean])
  implicit val NDRegisterDeviceResponseFormater = jsonFormat3(NDApiResponse[String])
  implicit val OAuth2CallbackFormater = jsonFormat5(OAuth2CallbackModel)
  implicit val GoogleTokenFormater = jsonFormat4(GoogleToken)


  //http PUT http://localhost:8080/registeruser < registeruser.json
  //http PUT http://localhost:8080/registerdevice < registerdevice.json

  //Heroku
  //http PUT http://dry-atoll-6423.herokuapp.com/register < register.json
  //http PUT http://dry-atoll-6423.herokuapp.com/registeruser < registeruser.json
  val route =
    path("registeruser") {
      entity(as[UserRegisterDTO]) { ent =>
        put {
          complete {
            RegisterUser(system, ent)
            //new NDApiResponse[String](ErrorStatus.None, "", RegisterUser(system, ent))
          }
        }
      }
    } ~
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
    } ~
      path("oauth2callback") {
        parameter("code") {
          code => {
            get {
              complete {
                code.toString()
              }
            }
          }
        }
      }
}