package api

import akka.actor.{ActorSystem, Actor}
import models.ndapidtos._
import utils.{NDApiLogging}
import models.{ErrorStatus, NDApiResponse}
import scala.slick.driver.PostgresDriver.simple._
import dal._
import models.auth._
import utils._
import java.util.UUID
import models.auth.User
import models.ndapidtos.UserRegisterModel
import models.ndapidtos.DeviceRegisterModel
import models.auth.UserDevice
import org.joda.time.DateTime

/**
 * Created by nikolatonkev on 2014-05-20.
 */
object RegisterActor extends NDApiLogging with NDApiUtil {


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

  def RegisterUser(system: ActorSystem, model: UserRegisterModel): String = {
    val database = dao.GetDataBase(system)
    if (UserExist(model.email, database)){
      "User already exist"
    } else
    {
      try {
        val userId = GetNewUUID
        val token = None
        val tokenexpirydate = None
        val Id = None
        val verifiedemail = None
        val givenname = None
        val surname = None
        val link = None
        val picture = None
        val gender = None
        val applicationId = UUID.fromString("e75b92a3-3299-4407-a913-c5ca196b3cab")
        val user = new User(userId, model.userName, Option(model.email), token, tokenexpirydate, Id, verifiedemail, givenname, surname, link, picture, gender, /* model.secretQuestion,model.secretAnswer, model.userPassword,*/ Option(applicationId))
        //(userId, model.userName, Option(model.email), Option(Some), Option(Some), Option(Some),
        //  Option(Some),Option(Some),Option(Some),Option(Some),Option(Some),Option(Some),Option(Some),Option(Some),
        //  Option(applicationId))
        println(user.toString)
        database.withSession{
          session => UsersDAL.insert(user)(session)
        }
        userId.toString()
      }
      catch {
        case e: Exception => {
          errorLogger.error(e.getStackTraceString)
        }
          "Error inserting a user"
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



  def Register(model: UserRegisterModel): Boolean = {
    true
  }

}

class RegisterActor extends Actor {

  import RegisterActor._

  val system = ActorSystem()
  def receive = {
    case (model: DeviceRegisterModel) => sender ! RegisterDevice(system, model)
    case (model: UserRegisterModel) => sender ! Register(model)
  }

}
