package dal

import scala.slick.driver.PostgresDriver.simple._
import models.auth._
import java.util.UUID

/**
 * Created by AMoroz on 20/05/2014.
 */

class UserTable(tag: Tag) extends Table[User](tag, Some("auth"), "user") {
  def * = (userid, username, userpassword, email, secretquestion, secretanswer, applicationId) <> (User.tupled, User.unapply)
  //def ? = (userid.?, username.?, userpassword.?, email, secretquestion, secretanswer/*, transactionid, systemstatusid.?*/).shaped.<>({r=>import r._; _1.map(_=> User.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  /** Database column userid PrimaryKey */
  val userid: Column[UUID] = column[UUID]("userid", O.PrimaryKey)
  /** Database column username  */
  val username: Column[String] = column[String]("username")
  /** Database column userpassword  */
  val userpassword: Column[String] = column[String]("userpassword")
  /** Database column email  */
  val email: Column[Option[String]] = column[Option[String]]("email")
  /** Database column secretquestion  */
  val secretquestion: Column[Option[String]] = column[Option[String]]("secretquestion")
  /** Database column secretanswer  */
  val secretanswer: Column[Option[String]] = column[Option[String]]("secretanswer")
  /** Database column applicationId  */
  val applicationId: Column[UUID] = column[UUID]("applicationid")

  /** Database column transactionid  */
  //val transactionid: Column[Option[Int]] = column[Option[Int]]("transactionid")
  /** Database column systemstatusid  */
  //val systemstatusid: Column[Int] = column[Int]("systemstatusid")
}



trait Users {
  //this: Profile =>

  def findByName(name: String)(implicit s: Session): Option[User]

  def findByEmail(email: String)(implicit s: Session): Option[User]

  def findById(id: UUID)(implicit s: Session): Option[User]

  def insert(u : User)(implicit s: Session)

  def delete(id: UUID)(implicit s: Session)

  def update(id: UUID, u: User)(implicit s: Session)

  def count(filter: String)(implicit s: Session): Int

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%")
          (implicit s: Session) : Page[(Any,Any,Any,Any,Any,Any,Any/*,Any,Any*/)]
}

object UsersDAL extends Users {

  val users = TableQuery[UserTable]

  def findByName(name: String)(implicit s: Session): Option[User] = {
    users.where(_.username === name).firstOption
  }

  def findByEmail(email: String)(implicit s: Session): Option[User] = {
    users.where(_.email === email).firstOption
  }

  def findById(id: UUID)(implicit s: Session): Option[User] = {
    users.where(_.userid === id).firstOption
  }

  def insert(u : User)(implicit s: Session) {
    users.insert(u)
  }

  def delete(id: UUID)(implicit s: Session) {
    users.where(_.userid === id).delete
  }

  def update(id: UUID, u: User)(implicit s: Session) {
    users.where(_.userid === id).update(u)
  }

  def count(filter: String)(implicit s: Session): Int = {
    users.where(_.username.toLowerCase like filter.toLowerCase).countDistinct.asInstanceOf[Int]
  }

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%")
          (implicit s: Session) : Page[(Any,Any,Any,Any,Any,Any,Any/*,Any,Any*/)] = {
    val offset: Int = pageSize * page
    val result = (for (u <- users) yield u).drop(offset).take(pageSize).list.map(
      row => (row.userid, row.username, row.userpassword, row.email, row.secretquestion, row.secretanswer, row.applicationid))//, row.transactionid, row.systemstatusid))
    val totalRows = count(filter)
    Page(result, page, offset, totalRows)
  }

}