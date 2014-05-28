package api

import akka.actor.{ActorSystem, Actor, ActorLogging}
import spray.routing.{Directives, HttpService}
import models.{ErrorStatus, NDApiResponse, Person}
import utils.NDApiLogging
import dal.{dao, Profile, Users}
import models.auth._

object TestActor extends Users with Profile with NDApiLogging {
  //case class Test
  //case class GetPerson(personId: Int)

  def _GetPerson(personId: Int): Person = {
    try{
      val name: Option[String] = Some("Desy")
      val family: Option[String] = Some("Slaveva")
      val age: Option[Int] = Some(38)
      val person = new Person(personId, name, family, age)
      person
    }
    catch {
      case e: Exception => {
        errorLogger.error(e.getStackTraceString)
      }
      null
    }
  }

  def GetPerson(personId: Int): NDApiResponse[Person] = {
    try
    {
      val name: Option[String] = Some("Desy")
      val family: Option[String] = Some("Slaveva")
      val age: Option[Int] = Some(38)
      val person = new Person(personId, name, family, age)
      new NDApiResponse[Person](ErrorStatus.None, "", person)
    }
    catch {
      case e: Exception => {
        errorLogger.error(e.getStackTraceString)
      }
      new NDApiResponse[Person](ErrorStatus.ErrorGettingData, "Error in function GetPerson: " + e.getLocalizedMessage(), null)
    }
  }

  def TestFunc(system: ActorSystem, email: String) = {
    val database = dao.GetDataBase(system)
    val user = database.withSession{
      session => findByEmail(email) (session)
    }
    user
  }

}

class TestActor extends Actor
{
  import TestActor._

  val system = ActorSystem()

  def receive = {
    case x: String => sender ! TestFunc(system, x)
    case x: Int => sender ! GetPerson(x)
  }


}
