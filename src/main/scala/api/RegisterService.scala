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

  //http PUT http://localhost:8080/registeruser < registeruser.json
  //http PUT http://localhost:8080/registerdevice < registerdevice.json

  //Heroku
  //http PUT http://dry-atoll-6423.herokuapp.com/register < register.json
  //http PUT http://dry-atoll-6423.herokuapp.com/registeruser < registeruser.json
  val route =
  /*
    path("registeruser") {
      post {
        parameter("access_token") {
          access_token => {
            onComplete(GetGoogleUserInfo(access_token)) {
              case Success(token) => {
                println("Succsess : " + token)
                RegisterUser(system,token)
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
  }~*/
  path("provisionOAuth2IDs"){
    get {
      complete(new OAuth2IDs("783241267105-s1si6l0t9h1dat18gih2j5bphg7st307.apps.googleusercontent.com","783241267105-bc7pq09tr1nnogat72r9tgmaeg2mre28.apps.googleusercontent.com"))
    }
  }~
  path("getuserinfo") {
    get {
      parameter("access_token") {
        access_token => {
          val gui:GoogleUserInfo = GetUserInfo(system,access_token)
          println("UserInfo retrieved : " + gui.id)
          complete(gui)
          }
        }
      }
  }~
  path("oauth2callback") {
    get {
      parameter("code") {
        code => {
            onComplete(GetGoogleAccessToken(code)) {
              case Success(token) => {
                println("OAuth2Callback success: " + token)
                onComplete(GetGoogleUserInfo(token.access_token)) {
                  case Success(ui_token) => {
                    println("GoogleUserInfo succsess : " + ui_token)
                    RegisterUser(system, token, ui_token)
                    complete(token)
                  }
                  case Failure(ex) => {
                    println("Failure : " + ex.toString())
                    complete(null)
                  }
                }
                //complete(token)
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