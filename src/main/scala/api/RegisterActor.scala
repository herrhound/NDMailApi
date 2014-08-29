package api

import akka.actor.{ActorSystem, Actor}
import models.ndapidtos._
import utils.{NDApiLogging}
import models.{GoogleToken, ErrorStatus, NDApiResponse}
import scala.slick.driver.PostgresDriver.simple._
import dal._
import models.auth._
import utils._
import java.util.UUID
import spray.http._
import scala.concurrent.Future
import spray.client.pipelining._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.http.HttpRequest
import spray.http.HttpResponse
import models.ndapidtos.UserRegisterDTO
import models.NDApiResponse
import models.auth.User
import models.GoogleToken
import models.ndapidtos.DeviceRegisterModel
import models.auth.UserDevice


/**
 * Created by nikolatonkev on 2014-05-20.
 */
object RegisterActor extends NDApiLogging with NDApiUtil with  DefaultJsonFormats {

  implicit val GoogleTokenFormater = jsonFormat4(GoogleToken)

  var apiAccessToken: Option[GoogleToken] = None

  def GetUserByEmail(email: String, database: Database) = {
    val user = database.withSession {
      val dal = UsersDAL
      session => dal.findByEmail (email) (session)
    }

    user
  }

  def UserExist(email: String, database: Database): Boolean = {
    val user = database.withSession {
      val dal = UsersDAL
      session => dal.findByEmail (email) (session)
    }
    !user.equals(None)
  }

  def DeviceExist(userId: UUID, deviceId: String, database: Database): Boolean = {
    val device = database.withSession {
      val dal = DeviceDAL
      val deviceUUID = UUID.fromString(deviceId)
      session => dal.findByMapping(userId, deviceUUID) (session)
    }
    !device.equals(None)
  }

  def GetUserDeviceMapping(userId: UUID, deviceId: UUID, d: Database) = {

    val mpng = d.withSession {
      val dal = UserDevicesDAL
      session => dal.findByMapping(userId, deviceId)(session)
    }

    mpng

  }

  def RegisterUser(system: ActorSystem, model: UserRegisterDTO): NDApiResponse[String] = {
    val database = dao.GetDataBase(system)
    if (UserExist(model.email.get, database)){
      new NDApiResponse[String](ErrorStatus.UserExists, "User already exist", "")
    }
    else {
      try {
        val userId = GetNewUUID
        val applicationId = UUID.fromString("e75b92a3-3299-4407-a913-c5ca196b3cab")
        val user = new User(userId, model.username, model.email, model.token, model.tokenexpirydate, model.Id, model.verifiedemail, model.givenname, model.surname, model.link, model.picture, model.gender, /* model.secretQuestion,model.secretAnswer, model.userPassword,*/ Option(applicationId))
        println(user.toString)
        database.withSession{
          session => UsersDAL.insert(user)(session)
        }
        new NDApiResponse[String](ErrorStatus.None, "", userId.toString())
      }
      catch {
        case e: Exception => {
          errorLogger.error(e.getStackTraceString)
        }
        new NDApiResponse[String](ErrorStatus.ErrorSavingData, "Error inserting a user", "")
      }
    }
  }

  def RegisterDevice(system: ActorSystem, model: DeviceRegisterModel): String = {
    try {
      val database = dao.GetDataBase(system)
      val user: Option[User] = GetUserByEmail(model.email, database)
      val userId: UUID = user.get.userid

      if(UserExist(model.email, database)) {
        if (!DeviceExist(userId, model.deviceUniqueId, database)) {
          MapDevice(system, model)
        }
        else
          "Device already registered"
      }
      else
        "User doesn't exist"

    }
    catch {
      case e: Exception => {
        errorLogger.error(e.getStackTraceString)
      }
    "00000000-0000-0000-0000-000000000000"
    }
  }

  def MapDevice(system: ActorSystem, model: DeviceRegisterModel): String = {
    try {
      val database = dao.GetDataBase(system)
      val user: Option[User] = GetUserByEmail(model.email, database)

      if(!user.equals(None)){
        val userid: UUID = user.get.userid
        val deviceid = java.util.UUID.fromString(model.deviceUniqueId)

        val mapping = GetUserDeviceMapping(userid, deviceid, database)
        if(mapping.equals(None)){
          val authguidId = GetNewUUID
          val userdevice = new UserDevice(None, userid, deviceid, authguidId, None)
          database.withSession { session => UserDevicesDAL.insert(userdevice)(session) }
          authguidId.toString()
        }
        else {
          mapping.get.authguid.toString()
        }
      }
      else
      {
        ""
      }
    }
    catch {
      case e: Exception => {
        errorLogger.error(e.getStackTraceString)
      }
      ""
    }
  }

  def GetGoogleAccessToken(code: String): Future[Option[GoogleToken]] = {
    val client_id = "783241267105-s1si6l0t9h1dat18gih2j5bphg7st307.apps.googleusercontent.com"
    val client_secret = "MbSGiXXwLPaanFbJSVseW9qs"
    val redirect_uri = "https://dry-atoll-6423.herokuapp.com/oauth2callback"
    val grant_type = "authorization_code"

    implicit val system = ActorSystem()
    import scala.concurrent.ExecutionContext.Implicits.global

    val pipeline: HttpRequest => Future[Option[GoogleToken]] = (
      encode(Gzip)
        ~> addHeader("Accept","application/json")
        ~> sendReceive
        ~> decode(Deflate)
        ~> unmarshal[Option[GoogleToken]]
      )
    pipeline(Post(s"https://accounts.google.com/o/oauth2/token?code=$code&client_id=$client_id&client_secret=$client_secret&redirect_uri=$redirect_uri&grant_type=$grant_type"))
  }

}

class RegisterActor extends Actor {

  import RegisterActor._

  val system = ActorSystem()
  def receive = {
    case (model: DeviceRegisterModel) => sender ! RegisterDevice(system, model)
    case (model: UserRegisterDTO) => sender ! RegisterUser(system, model)
    case (code: String) => sender ! GetGoogleAccessToken(code)
  }

}
