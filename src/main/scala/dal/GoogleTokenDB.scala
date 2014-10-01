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

class GoogleTokenTable(tag: Tag) extends Table[GoogleTokenInfo](tag, Some("auth"), "googletoken") {
  def * = (access_token, expires_in, id_token, refresh_token, token_type, userinfo_id, issued_on) <>
    (GoogleTokenInfo.tupled, GoogleTokenInfo.unapply)

  val access_token: Column[String] = column[String]("access_token")
  val expires_in: Column[Int] = column[Int]("expires_in")
  val id_token: Column[String] = column[String]("id_token")
  val refresh_token: Column[String] = column[String]("refresh_token")
  val token_type: Column[String] = column[String]("token_type")
  val userinfo_id: Column[String] = column[String]("userinfo_id")
  val issued_on: Column[DateTime] = column[DateTime]("issued_on")
}

trait GoogleTokens {
  //this: Profile =>

  def findByAccessToken(access_token: String)(implicit s: Session): Option[GoogleTokenInfo]

  def findById(userinfo_id: String)(implicit s: Session): Option[GoogleTokenInfo]

  def insert(token : GoogleTokenInfo)(implicit s: Session)

  def delete(access_token: String)(implicit s: Session)

  def update(access_token: String, token: GoogleTokenInfo)(implicit s: Session)

  def count(filter: String)(implicit s: Session): Int
}


object GoogleTokenDAL extends GoogleTokens {

  val gt = TableQuery[GoogleTokenTable]

  def findByAccessToken(access_token: String)(implicit s: Session): Option[GoogleTokenInfo] = {
    gt.where(_.access_token === access_token).firstOption
  }

  def findById(userinfo_id: String)(implicit s: Session): Option[GoogleTokenInfo] = {
    gt.where(_.userinfo_id === userinfo_id).firstOption
  }

  def insert(u : GoogleTokenInfo)(implicit s: Session) {
    gt.insert(u)
  }

  def delete(access_token: String)(implicit s: Session) {
    gt.where(_.access_token === access_token).delete
  }

  def update(access_token: String, token: GoogleTokenInfo)(implicit s: Session) {
    gt.where(_.access_token === access_token).update(token)
  }

  def count(filter: String)(implicit s: Session): Int = {
    gt.where(_.access_token.toLowerCase like filter.toLowerCase).countDistinct.asInstanceOf[Int]
  }
}