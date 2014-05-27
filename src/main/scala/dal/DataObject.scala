package dal

import java.util.Properties
import akka.actor.ActorSystem
import scala.slick.driver.PostgresDriver.simple._

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