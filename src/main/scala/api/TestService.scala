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
import models.auth._
import dal.{dao}

class TestService(system:ActorSystem, testing: ActorRef)(implicit context: ExecutionContext)
  extends Directives with  DefaultJsonFormats with AuthenticationDirectives
{
  //import dal.DataObject
  import scala.concurrent.duration._
  implicit val timeout = Timeout(5.seconds)

  implicit val PersonFormater = jsonFormat4(Person)
  implicit val NDRequestFormater = jsonFormat3(NDApiRequest[Person])
  implicit val NDResponseFormater = jsonFormat3(NDApiResponse[Person])
  implicit val UserFormater = jsonFormat8(User)


  //http GET http://localhost:8080/test
  //http GET http://dry-atoll-6423.herokuapp.com/test
  //http GET http://dry-atoll-6423.herokuapp.com/person < person_wrong_auth.json
  //http GET http://localhost:8080/person < person_auth_ok.json

  val route =
    path("test") {
      get {
          complete {
            TestFunc(system, "test@test.com")
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
                  GetPerson(1)
                 }
            }
          }
      }
    }
}
