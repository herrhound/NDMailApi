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

  def UserExist(email: String, database: Database): Boolean = {
    val user = database.withSession {
      session => findByEmail (email) (session)
    }

    !user.equals(None)
  }

  def UserDeviceExist(userId: UUID, deviceId: UUID, database: Database): Boolean = {
    val mapping: UserDevices = database.withSession {
      session => findMapping(userId, deviceId)(session).asInstanceOf[UserDevices]
    }

    !mapping.equals(None)
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
        val a = RegisterUser(model, database)
        val user = database.withSession{session => findByEmail(model.email)(session).asInstanceOf[User]}

        if(!user.equals(None)){
          val bb = UserDeviceExist(user.userid, java.util.UUID.fromString(model.deviceUniqueId), database)
          if(!bb){
            val userdevice = new UserDevices(0, user.userid, java.util.UUID.fromString(model.deviceUniqueId), GetNewUUID, DateTime.now())
            val b = database.withSession { session => insert(userdevice)(session) }

          }
        }
        true
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
