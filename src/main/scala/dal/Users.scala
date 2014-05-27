package dal
import scala.slick.driver.PostgresDriver.simple._
/**
 * Created by AMoroz on 20/05/2014.
 */

/*
object Users extends dal.DAL{
  //val profile = scala.slick.driver.PostgresDriver

  def findByName(name: String)(implicit s: Session){
    users.where(_.username === name).firstOption
  }

  def insert(u: UserRow)(implicit s: Session){
    users.insert(u)
  }
}
*/