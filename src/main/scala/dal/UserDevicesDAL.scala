package dal

import scala.slick.driver.PostgresDriver.simple._
import models.auth._
import java.util.UUID
import models._
import org.joda.time._
import com.github.tototoshi.slick.H2JodaSupport._
import scala.slick.driver.PostgresDriver


/**
 * Created by nikolatonkev on 2014-05-28.
 */
trait UserDevicesDAL extends Users {
  this: Profile =>

case class UserDevicesTable(tag: Tag) extends Table[UserDevices](tag, Some("auth"), "userdevices") {

  def userdevicesid = column[Int]("userdevicesid", O.PrimaryKey, O.AutoInc)
  def userid = column[UUID]("userid", O.NotNull)
  def deviceid = column[UUID]("deviceid", O.NotNull)
  def authguid = column[UUID]("authguid", O.NotNull)
    //val expiredate: Column[DateTime] = column[DateTime]("expiredate")

  def * = (userdevicesid.?, userid, deviceid, authguid) <> (UserDevices.tupled, UserDevices.unapply)

  /** Maps whole row to an option. Useful for outer joins. */
  //def ? = (userdevicesid.?, userid, deviceid, authguid).shaped.<>({r=>import r._; _1.map(_=> UserDevices.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  def user = foreignKey("userdevices_userid_fkey", userid, users)(_.userid)
  }

  val userdevices = TableQuery[UserDevicesTable]


  def findByMapping(uid: UUID, did: UUID)(implicit s: Session): Option[UserDevices] = {
    //userdevices.where(_.userid === userid).where(_.deviceid === deviceid).firstOption
    userdevices.where(_.userid === uid).firstOption
  }

  def withSession(function: (Nothing) => PostgresDriver.InsertInvoker[UserDevicesTable#TableElementType]#SingleInsertResult): Int = ???

  def insert(ud : UserDevices)(implicit s: Session): Int = {
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
