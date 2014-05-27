package api

import akka.actor.{ActorSystem, Actor}
import models.ndapidtos.{RegisterModel, DeviceRegisterModel}
import utils.{NDApiLogging}
import models.{ErrorStatus, NDApiResponse}
import scala.slick.driver.PostgresDriver.simple._
import dal.{dao}
import java.util.UUID
import utils._

/**
 * Created by nikolatonkev on 2014-05-20.
 */
object RegisterActor extends NDApiLogging with NDApiUtil {

  def UserExist(email: String, database: Database): Boolean = {
    val user = database.withSession {
      session => dal.DAL.findByEmail (email) (session)
    }

    !user.equals(None)
  }

  def RegisterUser(model: DeviceRegisterModel, database: Database): Boolean = {
    try {
      val userId = GetNewUUID
      val user = new dal.DAL.UserRow(userId, model.email, model.email, Option(model.email),Option(""),Option(""),Option(2),1 )
      database.withSession{session => dal.DAL.insert(user)(session)}
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
      if(!UserExist(model.email, database)){
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
