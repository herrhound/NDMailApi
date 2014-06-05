package Auth

import models.{ErrorStatus, AuthTokens}
import spray.routing.authentication.Authentication
import spray.routing.authentication.ContextAuthenticator
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import models.ErrorStatus.ErrorStatus
import spray.routing.AuthenticationFailedRejection
import models.AuthTokens
import spray.routing.AuthenticationFailedRejection._
import dal.{UserDevicesDAL, dao}
import models.auth.UserDevice
import akka.actor.ActorSystem


trait AuthenticationDirectives {

  def authenticateUser(tokens: AuthTokens): ContextAuthenticator[ErrorStatus] = {
    ctx =>
    {
      doAuth(tokens)
    }
  }

  private def doAuth(tokens: AuthTokens): Future[Authentication[ErrorStatus]] = {
    Future {
      def CheckTokens(tokens: AuthTokens) = {
        //val result = isAuthenticated(tokens)
        //true

        val system = ActorSystem()

        val database = dao.GetDataBase(system)
        val success = database.withSession {
          session => UserDevicesDAL.isAuth(tokens) (session)
        }

        success

      }

      /*1
      if(CheckTokens(tokens))
        Right(ErrorStatus.None)
      else
        Left(ErrorStatus.NotAuthenticated)
    */

      Either.cond(CheckTokens(tokens),
        ErrorStatus.None, AuthenticationFailedRejection(CredentialsRejected, Nil))
    }
  }

}
