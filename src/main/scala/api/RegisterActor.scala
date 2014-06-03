package api

import akka.actor.{ActorSystem, Actor}
import models.ndapidtos.{RegisterModel, DeviceRegisterModel}
import utils.{NDApiLogging}
import models.{ErrorStatus, NDApiResponse}
import scala.slick.driver.PostgresDriver.simple._
import dal.{Profile, Users, UserDevicesDAL, dao}
import models.auth._
import utils._
import java.util.UUID
import org.joda.time.DateTime

/**
 * Created by nikolatonkev on 2014-05-20.
 */
object RegisterActor extends Users with UserDevicesDAL with Profile with NDApiLogging with NDApiUtil {


  def GetUserByEmail(email: String, database: Database) = {
    val user = database.withSession {
      session => findByEmail (email) (session)
    }

    user
  }

  def UserExist(email: String, database: Database): Boolean = {
    val user = database.withSession {
      session => findByEmail (email) (session)
    }

    !user.equals(None)
  }

  def GetUserDeviceMapping(userId: UUID, deviceId: UUID, database: Database): Option[UserDevices] = {
    database.withSession {
      session => findByMapping(userId, deviceId)(session)
    }
  }

  def RegisterUser(model: DeviceRegisterModel, database: Database): Boolean = {
    try {
      val userId = GetNewUUID
      val user = new User(userId, model.email, model.email, Option(model.email),Option(""),Option(""),Option(2),1 )
      database.withSession{session => insert(user)(session)}
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
      Console.println("In MapDevice...")
      val database = dao.GetDataBase(system)
      val user: Option[User] = GetUserByEmail(model.email, database)

      if(!user.equals(None)){
          //val userid: UUID = user.asInstanceOf[User].userid
          val userid: UUID = user.get.userid
          //val mapping: Option[UserDevices]  = GetUserDeviceMapping(userid, java.util.UUID.fromString(model.deviceUniqueId), database)
          //if(mapping.equals(None)){
            val authguidId = GetNewUUID
            val userdevice = new UserDevices(0, userid, java.util.UUID.fromString(model.deviceUniqueId), authguidId)
            database.withSession { session => insert(userdevice)(session) }
            authguidId.toString()
          //}
          //else{
          //  mapping.asInstanceOf[UserDevices].authguid.toString()
          //}
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
