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
/*    path("registeruser") {
      entity(as[UserRegisterDTO]) { ent =>
        post {
          complete {
            RegisterUser(system, ent)
            //new NDApiResponse[String](ErrorStatus.None, "", RegisterUser(system, ent))
          }
        }
      }
    } ~
    path("registerdevice") {
      entity(as[DeviceRegisterModel]) { ent =>
        post {
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
    } ~*/

  path("oauth2callback") {
    get {
      parameter("code") {
        code => {
          //complete("Hello: " + code.toString())


          val result = GetGoogleAccessToken(code).onComplete {
              case Success(token) => {
                println("Success : " + token)
                //val stkn = new NDApiResponse[GoogleToken](ErrorStatus.None, "Authenticated", token)
                //stkn
                //complete(token.asInstanceOf[GoogleToken])
                //token.asInstanceOf[GoogleToken]
                new NDApiResponse[GoogleToken](ErrorStatus.None, "Authenticated", token)
              }
              case Failure(ex) => {
                println("Failure : " + ex.toString())
                //val ftkn = new NDApiResponse[GoogleToken](ErrorStatus.NotAutorized, ex.toString(), new GoogleToken("",0,"","",""))
                //ftkn
                //null
                new NDApiResponse[GoogleToken](ErrorStatus.NotAutorized, ex.toString(), new GoogleToken("",0,"","",""))
              }

            }
            //.asInstanceOf[NDApiResponse[GoogleToken]]

          val response =  result.asInstanceOf[GoogleToken]

          println("result: " + response.toString())
          complete(response)

            /*
            Null => {
              val result2 = new NDApiResponse[String](ErrorStatus.NotAuthenticated, "Not authenticated", "")
              complete(result2)
            }
            */
        }
      }
    }
  }
}