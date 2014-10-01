package dal

import scala.slick.driver.PostgresDriver.simple._
import models.auth._
import models.GoogleJsonProtocol._
import java.util.UUID
import org.joda.time._
import com.github.tototoshi.slick.PostgresJodaSupport._

/**
 * Created by moral10 on 14-09-22.
 */

class UserInfoTable(tag: Tag) extends Table[GoogleUserInfo](tag, Some("auth"), "userinfo") {
  def * = (id, family_name, gender, given_name, link, locale, name, picture) <> (GoogleUserInfo.tupled, GoogleUserInfo.unapply)

  val id: Column[String] = column[String]("id")
  val family_name: Column[String] = column[String]("family_name")
  val gender: Column[String] = column[String]("gender")
  val given_name: Column[String] = column[String]("given_name")
  val link: Column[String] = column[String]("link")
  val locale: Column[String] = column[String]("locale")
  val name: Column[String] = column[String]("name")
  val picture: Column[String] = column[String]("picture")
}

trait UserInfos {
  //this: Profile =>

  def findByName(name: String)(implicit s: Session): Option[GoogleUserInfo]

  def findById(id: String)(implicit s: Session): Option[GoogleUserInfo]

  def insert(u : GoogleUserInfo)(implicit s: Session)

  def delete(id: String)(implicit s: Session)

  def update(id: String, u: GoogleUserInfo)(implicit s: Session)

  def count(filter: String)(implicit s: Session): Int
}


object UserInfoDAL extends UserInfos {

  val users = TableQuery[UserInfoTable]

  def findByName(name: String)(implicit s: Session): Option[GoogleUserInfo] = {
    users.where(_.name === name).firstOption
  }

  def findById(id: String)(implicit s: Session): Option[GoogleUserInfo] = {
    users.where(_.id === id).firstOption
  }

  def insert(u : GoogleUserInfo)(implicit s: Session) {
    users.insert(u)
  }

  def delete(id: String)(implicit s: Session) {
    users.where(_.id === id).delete
  }

  def update(id: String, u: GoogleUserInfo)(implicit s: Session) {
    users.where(_.id === id).update(u)
  }

  def count(filter: String)(implicit s: Session): Int = {
    users.where(_.name.toLowerCase like filter.toLowerCase).countDistinct.asInstanceOf[Int]
  }
}