package dal

import scala.slick.driver.PostgresDriver.simple._
import models.auth._
import java.util.UUID
import org.joda.time._
import com.github.tototoshi.slick.H2JodaSupport._
import models.AuthTokens


/**
 * Created by nikolatonkev on 2014-05-28.
 */

class UserDevicesTable(tag: Tag) extends Table[UserDevice](tag, Some("auth"), "userdevices") {

  def * = (userdevicesid.?, userid, deviceid, authguid) <> (UserDevice.tupled, UserDevice.unapply)
  //def * = (userdevicesid, userid, deviceid, authguid) <> (UserDevice.tupled, UserDevice.unapply)
  //def ? = (userdevicesid, userid.?, deviceid.?, authguid.?, expiredate).shaped.<>({r=>import r._; _1.map(_=> UserDevice.tupled((_1, _2.get, _3.get, _4.get, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  val userdevicesid: Column[Int] = column[Int]("userdevicesid", O.PrimaryKey, O.AutoInc)
  val userid: Column[UUID] = column[UUID]("userid")
  val deviceid: Column[UUID] = column[UUID]("deviceid")
  val authguid: Column[UUID] = column[UUID]("authguid")
  //val expiredate: Column[Option[DateTime]] = column[Option[DateTime]]("expiredate")

  def user = foreignKey("userdevices_userid_fkey", userid, UsersDAL.users)(_.userid)
}

trait UserDevices {
  //this: Profile =>

  //def findByMapping(uid: UUID, did: UUID)(implicit s: Session): Option[UserDevices]
  def findByMapping(uid: UUID, did: UUID)(implicit s: Session): Option[UserDevice]

  def isAuth(tokens: AuthTokens)(implicit s: Session): Boolean

  def insert(ud : UserDevice)(implicit s: Session)

  /*
  def delete(id: UUID)(implicit s: Session)

  def update(id: UUID, u: User)(implicit s: Session)
  */


}

object UserDevicesDAL extends UserDevices {
  val userdevices = TableQuery[UserDevicesTable]

  def findByMapping(uid: UUID, did: UUID)(implicit s: Session): Option[UserDevice] = {
    userdevices.where(x => x.userid === uid && x.deviceid === did).firstOption
  }

  def isAuth(tokens: AuthTokens)(implicit s: Session): Boolean = {
    val model = userdevices.where(x => x.authguid === tokens.AuthGuyd && x.deviceid === tokens.DeviceUniqueId ).firstOption

    !model.equals(None)
  }

  def insert(ud : UserDevice)(implicit s: Session) {
    //userdevices.insert(ud)
    userdevices.map(s => (s.userid, s.deviceid, s.authguid)).insert(ud.userid, ud.deviceid, ud.authguid)
  }

  /*
  def delete(id: UUID)(implicit s: Session) {
    users.where(_.userid === id).delete
  }

  def update(id: UUID, u: User)(implicit s: Session){
    users.where(_.userid === id).update(u)
  }
  */

}