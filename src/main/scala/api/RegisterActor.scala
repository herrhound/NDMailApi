package api

import akka.actor.{ActorSystem, Actor}
import models.ndapidtos.{RegisterModel, DeviceRegisterModel}
import utils.{NDApiLogging}
import models.{ErrorStatus, NDApiResponse}
import scala.slick.driver.PostgresDriver.simple._
import dal._
import models.auth._
import utils._
import java.util.UUID
import models.auth.User
import models.ndapidtos.RegisterModel
import models.ndapidtos.DeviceRegisterModel
import models.auth.UserDevice

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

  def GetUserDeviceMapping(userId: UUID, deviceId: UUID, d: Database) = {

    val mpng = d.withSession {
      val dal = UserDevicesDAL
      session => dal.findByMapping(userId, deviceId)(session)
    }

    mpng

  }

  def RegisterUser(model: DeviceRegisterModel, database: Database): Boolean = {
    try {
      val userId = GetNewUUID
      val applicationId = UUID.fromString("e75b92a3-3299-4407-a913-c5ca196b3cab")
      val user = new User(userId, model.email, model.email, Option(model.email),Option(""), Option("")/* applicationId*/)
      database.withSession{
        //val dal = UsersDAL
        session => UsersDAL.insert(user)(session)
      }
      true
    }
    catch {
      case e: Exception => {
        errorLogger.error(e.getStackTraceString)
      }
        false
    }
  }

  def RegisterDevice(system: ActorSystem, model: DeviceRegisterModel): Boolean = {
    try {
      val database = dao.GetDataBase(system)
      if(!UserExist(model.email, database)) {
        RegisterUser(model, database)
      }
      else
      {
        false
      }
    }
    catch {
      case e: Exception => {
        errorLogger.error(e.getStackTraceString)
      }
      false
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



  def Register(model: RegisterModel): Boolean = {
    true
  }

}

class RegisterActor extends Actor {

  import RegisterActor._

  val system = ActorSystem()
  def receive = {
    case (model: DeviceRegisterModel) => sender ! RegisterDevice(system, model)
    case (model: RegisterModel) => sender ! Register(model)
  }

}
