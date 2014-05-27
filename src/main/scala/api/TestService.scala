package api

import akka.actor.{ActorSystem, ActorRef}
import akka.util.Timeout
import models._

import spray.routing._
import scala.concurrent.ExecutionContext
import api.TestActor._
import Auth.{AuthenticationDirectives}
import models.NDApiRequest
import models.Person
import models.AuthTokens
import java.util.UUID
import dal.{dao}

//import dal.DataObject

class TestService(system:ActorSystem, testing: ActorRef)(implicit context: ExecutionContext)
  extends Directives with  DefaultJsonFormats with AuthenticationDirectives
{
  //import dal.DataObject
  import scala.concurrent.duration._
  implicit val timeout = Timeout(5.seconds)

  implicit val PersonFormater = jsonFormat4(Person)
  implicit val NDRequestFormater = jsonFormat3(NDApiRequest[Person])
  implicit val NDResponseFormater = jsonFormat3(NDApiResponse[Person])


  //http GET http://localhost:8080/test
  //http GET http://localhost:8080/person < person_wrong_auth.json
  //http GET http://localhost:8080/person < person_auth_ok.json

  val route =
    path("test") {
      get {
        //handleWith { tt: Test => testing ! tt }
        /* Works fine */

        try {
          //val URL = "jdbc:postgresql://ec2-54-221-223-92.compute-1.amazonaws.com:5432/db7k8198l73h6l"
          //val username = "aypkpqlvwdznkk"
          //val password = "blItMMzvKwWjEFI1ItcWhc-uix"
          //val props = new Properties
          //props.setProperty("ssl", "true")
          //props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory")

          //val database = Database.forURL(URL, username, password, props)
          //val query = for (u <- dal.DAL.users) yield u.email

          //val u = new dal.DAL.UserRow(UUID.fromString("550e8400-e29b-41d4-a716-446655440111"),"test1","abc",Option("test1@test.com"),Option(""),Option(""),Option(2),1 )
          //database.withSession{session => dal.DAL.insert(u)(session)}

          //database.withSession{session => dal.DAL.delete(UUID.fromString("550e8400-e29b-41d4-a716-446655440111"))(session)}

          val database = dao.GetDataBase(system)
          val result = database.withSession{
            //session => query.list() (session)
            //session => dal.DAL.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")) (session)
            session => dal.DAL.findByEmail("test@test.com") (session)
          }

          println(result)

        } catch {
          case e: Exception =>
            println(e.getStackTraceString)
        }
          complete {
            "Test route OK"
          }
      }
    }~
    path("person") {
      entity(as[NDApiRequest[Person]]) { ent =>
          get {
            val tokens = new AuthTokens(ent.AuthGuyd, ent.DeviceUniqueId)
            authenticate(authenticateUser(tokens)) {
              st =>
                complete {
                  /*
                  val person = GetPerson(1)
                  val response = new NDApiResponse[Person](ErrorStatus.None, "", person)
                  response
                  */
                  GetPerson(1)
                 }
            }
          }
      }
    }
}
