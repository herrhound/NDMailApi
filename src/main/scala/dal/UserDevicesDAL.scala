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
trait UserDevicesDAL {
  this: Profile =>

  class UserDevicesTable(tag: Tag) extends Table[UserDevices](tag, Some("auth"), "userdevices") {
    def * = (userdevicesid, userid, deviceid, authguid, expiredate) <> (UserDevices.tupled, UserDevices.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (userdevicesid.?, userid.?, deviceid.?, authguid.?, expiredate.?).shaped.<>({r=>import r._; _1.map(_=> UserDevices.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column userdevicesid PrimaryKey */
    val userdevicesid: Column[Int] = column[Int]("userdevicesid", O.PrimaryKey)
    /** Database column userid */
    val userid: Column[UUID] = column[UUID]("userid")
    /** Database column deviceid  */
    val deviceid: Column[UUID] = column[UUID]("deviceid")
    /** Database column authguid  */
    val authguid: Column[UUID] = column[UUID]("authguid")
    /** Database column expiredate  */
    val secretanswer: Column[Option[String]] = column[Option[String]]("secretanswer")
    /** Database column transactionid  */
    val expiredate: Column[DateTime] = column[DateTime]("expiredate")
  }

  val userdevices = TableQuery[UserDevicesTable]


  def findMapping(userid: UUID, deviceid: UUID)(implicit s: Session): Option[UserDevices] = {
    userdevices.where(_.userid === userid).where(_.deviceid === deviceid).firstOption
  }

  /*
  def insert(u : User)(implicit s: Session) {
    users.insert(u)
  }

  def delete(id: UUID)(implicit s: Session) {
    users.where(_.userid === id).delete
  }

  def update(id: UUID, u: User)(implicit s: Session){
    users.where(_.userid === id).update(u)
  }
  */


}
