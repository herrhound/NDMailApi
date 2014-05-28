package dal

import java.util.Properties
import akka.actor.ActorSystem
import scala.slick.driver.PostgresDriver.simple._


trait Profile {
  val profile = scala.slick.driver.PostgresDriver
}

object dao {
  //implicit def system: ActorSystem

  implicit def GetDataBase(system: ActorSystem): Database = {
    val url = system.settings.config.getString("database.url")
    val username = system.settings.config.getString("database.username")
    val password = system.settings.config.getString("database.password")
    val ssl = system.settings.config.getString("database.ssl")
    val sslfactory = system.settings.config.getString("database.sslfactory")

    val props = new Properties
    props.setProperty("ssl", ssl)
    props.setProperty("sslfactory", sslfactory)

    val database: Database = Database.forURL(url, username, password, props)
    database
  }

}

case class Page[A] (items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}
