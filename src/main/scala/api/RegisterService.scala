package api

import akka.actor.{Props, ActorSystem, ActorRef}
import scala.concurrent.{Future, ExecutionContext}
import spray.routing.Directives

import api.RegisterActor._
import models.{OAuth2CallbackModel, ErrorStatus, NDApiResponse}
import models.GoogleJsonProtocol._


import models.ndapidtos.UserRegisterDTO
import models.NDApiResponse
import models.ndapidtos.DeviceRegisterModel
import models.OAuth2CallbackModel
import scala.util.{ Success, Failure }
import spray.http.HttpEntity

/**
 * Created by nikolatonkev on 2014-05-20.
 */

class RegisterService(system: ActorSystem, registering: ActorRef)(implicit context: ExecutionContext)
  extends Directives with DefaultJsonFormats  {

  import ExecutionContext.Implicits.global

  implicit val DeviceRegisterFormater = jsonFormat2(DeviceRegisterModel)
  implicit val RegisterFormater = jsonFormat12(UserRegisterDTO)
  implicit val NDResponseFormater = jsonFormat3(NDApiResponse[Boolean])
  implicit val NDRegisterDeviceResponseFormater = jsonFormat3(NDApiResponse[String])
  implicit val OAuth2CallbackFormater = jsonFormat5(OAuth2CallbackModel)
  implicit val GoogleTokenFormater = jsonFormat5(GoogleToken)

  implicit val OAuth2CallbackSuccessFormater = jsonFormat3(NDApiResponse[GoogleToken])
  implicit val OAuth2CallbackFailureFormater = jsonFormat3(NDApiResponse[String])
  import spray.httpx.encoding._

  //http PUT http://localhost:8080/registeruser < registeruser.json
  //http PUT http://localhost:8080/registerdevice < registerdevice.json

  //Heroku
  //http PUT http://dry-atoll-6423.herokuapp.com/register < register.json
  //http PUT http://dry-atoll-6423.herokuapp.com/registeruser < registeruser.json
  val route =
    path("registeruser") {
      post {
        parameter("access_token") {
          access_token => {
            onComplete(GetGoogleUserInfo(access_token)) {
              case Success(token) => {
                println("Succsess : " + token)
                complete(token)
              }
              case Failure(ex) => {
                println("Failure : " + ex.toString())
                complete(null)
              }
            }
          }
        }
        /*
        entity(as[UserRegisterDTO]) { ent =>
          post {
            complete {
              RegisterUser(system, ent)
              //new NDApiResponse[String](ErrorStatus.None, "", RegisterUser(system, ent))
            }
          }
        }
        */
      }
  }~
  path("oauth2callback") {
    get {
      parameter("code") {
        code => {
            onComplete(GetGoogleAccessToken(code)) {
              case Success(token) => {
                println("Success : " + token)
                complete(token)
              }
              case Failure(ex) => {
                println("Failure : " + ex.toString())
                complete(null)
              }
            }
        }
      }
    }
  }
}