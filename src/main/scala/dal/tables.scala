package dal

import java.util.UUID
import utils._


object DAL extends {
  val profile = scala.slick.driver.PostgresDriver
} with tables

case class Page[A] (items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

trait tables extends NDApiLogging{
  val profile: scala.slick.driver.PostgresDriver
  import profile.simple._
  import scala.slick.jdbc.{GetResult => GR}


  /** Entity class storing rows of table User
    *  @param userid Database column userid PrimaryKey
    *  @param username Database column username
    *  @param userpassword Database column userpassword
    *  @param email Database column email
    *  @param secretquestion Database column secretquestion
    *  @param secretanswer Database column secretanswer
    *  @param transactionid Database column transactionid
    *  @param systemstatusid Database column systemstatusid  */
  case class UserRow(userid: UUID, username: String, userpassword: String, email: Option[String], secretquestion: Option[String], secretanswer: Option[String], transactionid: Option[Int], systemstatusid: Int)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */

  /*
  implicit def GetResultUserRow(implicit e0: GR[UUID], e1: GR[Option[String]], e2: GR[Option[Int]], e3: GR[Int]): GR[UserRow] = GR{
    prs => import prs._
      UserRow.tupled((<<[UUID], <<[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[Int], <<[Int]))
  }
  */
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(tag: Tag) extends Table[UserRow](tag, Some("auth"), "user") {
    def * = (userid, username, userpassword, email, secretquestion, secretanswer, transactionid, systemstatusid) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (userid.?, username.?, userpassword.?, email, secretquestion, secretanswer, transactionid, systemstatusid.?).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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
    /** Database column transactionid  */
    val transactionid: Column[Option[Int]] = column[Option[Int]]("transactionid")
    /** Database column systemstatusid  */
    val systemstatusid: Column[Int] = column[Int]("systemstatusid")
  }

  val users = TableQuery[User]

  def findByName(name: String)(implicit s: Session): Option[UserRow] = {
    users.where(_.username === name).firstOption
  }

  def findByEmail(email: String)(implicit s: Session): Option[UserRow] = {
    users.where(_.email === email).firstOption
  }

  def findById(id: UUID)(implicit s: Session): Option[UserRow] = {
    users.where(_.userid === id).firstOption
  }

  def insert(u : UserRow)(implicit s: Session) {
    users.insert(u)
  }

  def delete(id: UUID)(implicit s: Session) {
    users.where(_.userid === id).delete
  }

  def update(id: UUID, u: UserRow)(implicit s: Session){
    users.where(_.userid === id).update(u)
  }

  def count(filter: String)(implicit s: Session): Int = {
    users.where(_.username.toLowerCase like filter.toLowerCase).countDistinct.asInstanceOf[Int]
  }

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%")
          (implicit s: Session) : Page[(Any,Any,Any,Any,Any,Any,Any,Any)] = {
    val offset: Int = pageSize * page
    val result = (for (u <- users) yield u).drop(offset).take(pageSize).list.map(
      row => (row.userid, row.username, row.userpassword, row.email, row.secretquestion, row.secretanswer, row.transactionid, row.systemstatusid))
    val totalRows = count(filter)
    Page(result, page, offset, totalRows)
 }

}
