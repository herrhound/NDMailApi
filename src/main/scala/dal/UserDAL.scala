package dal

import scala.slick.driver.PostgresDriver.simple._
import models.auth._
import java.util.UUID
import org.joda.time._
import com.github.tototoshi.slick.PostgresJodaSupport._

/**
 * Created by AMoroz on 20/05/2014.
 */

class UserTable(tag: Tag) extends Table[User](tag, Some("auth"), "user") {
  def * = (userid, username, email.?, token.?, tokenexpirydate.?, Id.?, verifiedemail.?, givenname.?, surname.?, link.?, picture.?, gender.?, applicationId.?) <> (User.tupled, User.unapply)
  /*secretquestion.?, secretanswer.?, userpassword, */

  val userid: Column[UUID] = column[UUID]("user_id")
  val username: Column[String] = column[String]("username")
  val email: Column[String] = column[String]("email")
  val token: Column[String] = column[String]("token")
  val tokenexpirydate: Column[DateTime] = column[DateTime]("tokenexpirydate")
  val Id: Column[Int] = column[Int]("id")
  val verifiedemail: Column[Boolean] = column[Boolean]("verifiedemail")
  val givenname: Column[String] = column[String]("givenname")
  val surname: Column[String] = column[String]("surname")
  val link: Column[String] = column[String]("link")
  val picture: Column[String] = column[String]("picture")
  val gender: Column[Int] = column[Int]("gender")
  val applicationId: Column[UUID] = column[UUID]("application_id")
  //val secretquestion: Column[Option[String]] = column[Option[String]]("secretquestion")
  //val secretanswer: Column[Option[String]] = column[Option[String]]("secretanswer")
  //val userpassword: Column[String] = column[String]("userpassword")
}



//,
//FOREIGN KEY ("26") REFERENCES application (application_id)
//MATCH FULL ON UPDATE NO ACTION ON DELETE NO ACTION


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
          (implicit s: Session) : Page[(Any,Any,Any,Any)]
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
          (implicit s: Session) : Page[(Any,Any,Any,Any)] = {
    val offset: Int = pageSize * page
    val result = (for (u <- users) yield u).drop(offset).take(pageSize).list.map(
      row => (row.userid, row.username, row.email, row.applicationid))//, row.transactionid, row.systemstatusid))
    val totalRows = count(filter)
    Page(result, page, offset, totalRows)
  }

}