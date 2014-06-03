package dal

import scala.slick.driver.PostgresDriver.simple._
import models.auth._
import java.util.UUID
import models._
import org.joda.time._
import com.github.tototoshi.slick.H2JodaSupport._


/**
 * Created by nikolatonkev on 2014-05-28.
 */
trait UserDevicesDAL extends Users {
  this: Profile =>

  class UserDevicesTable(tag: Tag) extends Table[UserDevices](tag, Some("auth"), "userdevices") {
    def * = (userdevicesid, userid, deviceid, authguid) <> (UserDevices.tupled, UserDevices.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (userdevicesid.?, userid.?, deviceid.?, authguid.?).shaped.<>({r=>import r._; _1.map(_=> UserDevices.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    val userdevicesid: Column[Int] = column[Int]("userdevicesid", O.PrimaryKey)
    val userid: Column[UUID] = column[UUID]("userid")
    val deviceid: Column[UUID] = column[UUID]("deviceid")
    val authguid: Column[UUID] = column[UUID]("authguid")
    //val expiredate: Column[DateTime] = column[DateTime]("expiredate")

    def user = foreignKey("userdevices_userid_fkey", userid, users)(_.userid)
  }

  val userdevices = TableQuery[UserDevicesTable]


  def findByMapping(uid: UUID, did: UUID)(implicit s: Session): Option[UserDevices] = {
    //userdevices.where(_.userid === userid).where(_.deviceid === deviceid).firstOption
    userdevices.where(_.userid === uid).firstOption
  }

  def insert(ud : UserDevices)(implicit s: Session) {
    userdevices.insert(ud)
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
