package Auth

import models.{ErrorStatus, AuthTokens}
import spray.routing.authentication.Authentication
import spray.routing.authentication.ContextAuthenticator
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import spray.http._
import java.util.UUID
import models.ErrorStatus.ErrorStatus
import spray.routing.AuthenticationFailedRejection
import models.AuthTokens
import spray.routing.AuthenticationFailedRejection._


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
        val u1: UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        val u2: UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440777");

        val result = ((tokens.AuthGuyd.equals(u1)) && (tokens.DeviceUniqueId.equals(u2)))
        result
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
