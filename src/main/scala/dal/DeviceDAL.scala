package dal

import scala.slick.driver.PostgresDriver.simple._
import models.auth._
import java.util.UUID
import models.AuthTokens
import scala.math.Ordering

/**
 * Created by Ruben on 2014-06-20.
 */

class DeviceTable(tag: Tag) extends Table[Device](tag, Some("auth"), "device") {

  def * = (deviceid, userid, deviceuniqueid, devicetype) <> (Device.tupled, Device.unapply)

  val deviceid: Column[UUID] = column[UUID]("deviceid", O.PrimaryKey)
  val userid: Column[UUID] = column[UUID]("userid")
  val deviceuniqueid: Column[String] = column[String]("deviceuniqueid")
  val devicetype: Column[String] = column[String]("devicetype")

  def user = foreignKey("device_userid_fkey", userid, UsersDAL.users)(_.userid)

}

trait Devices {

  def findByMapping(uid: UUID, did: UUID)(implicit s: Session): Option[Device]

  //def isAuth(tokens: AuthTokens)(implicit s: Session): Boolean

  def insert(ud : Device)(implicit s: Session)

  /*
  def delete(id: UUID)(implicit s: Session)

  def update(id: UUID, u: User)(implicit s: Session)
  */

}

object DeviceDAL extends Devices{
  val device = TableQuery[DeviceTable]

  def findByMapping(uid: UUID, did: UUID)(implicit s: Session): Option[Device] = {
    device.where(x => x.userid === uid && x.deviceid === did).firstOption
  }
/*
  def isAuth(tokens: AuthTokens)(implicit s: Session): Boolean = {
    val model = device.where(x => x.deviceid === tokens.DeviceUniqueId ).firstOption

    !model.equals(None)
  }
*/
  def insert(ud : Device)(implicit s: Session) {
    device.insert(ud)
    //userdevices.map(s => (s.userid, s.deviceid, s.authguid)).insert(ud.userid, ud.deviceid, ud.authguid)
  }

  /*
  def delete(id: UUID)(implicit s: Session) {
    device.where(_.userid === id).delete
  }

  def update(id: UUID, u: User)(implicit s: Session){
    device.where(_.userid === id).update(u)
  }
  */

}

