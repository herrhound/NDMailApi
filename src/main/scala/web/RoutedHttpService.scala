package web

import spray.http.StatusCodes._
import spray.http._
import spray.routing._
import spray.routing.AuthenticationFailedRejection._
import spray.util.{LoggingContext, SprayActorLogging}
import scala.util.control.NonFatal
import api.DefaultJsonFormats
import models.{ErrorStatus, NDApiResponse}
import akka.actor.Actor

import org.slf4j.LoggerFactory
import utils.NDApiLogging

case class ErrorResponseException(responseStatus: StatusCode, response: Option[HttpEntity]) extends Exception

class RoutedHttpService(route: Route) extends Actor with HttpService with akka.actor.ActorLogging with DefaultJsonFormats with NDApiLogging {

  implicit val NDResponseFormater = jsonFormat3(NDApiResponse[String])

  implicit def actorRefFactory = context

  //implicit def logger = LoggerFactory.getLogger("auth-rolling")

  implicit val NDRejectionHandler = RejectionHandler{
    case AuthenticationFailedRejection(CredentialsRejected, realm) :: _ => ctx => {
      authLogger.info("This request have not been authorized. Credentials are rejected.")
      val response = new NDApiResponse[String](ErrorStatus.NotAuthenticated, "This request have not been authorized. Credentials are rejected.", "")
      ctx.complete(response)
    }
  }

  implicit val handler = ExceptionHandler {
    case NonFatal(ErrorResponseException(statusCode, entity)) => ctx =>
      ctx.complete(statusCode, entity)

    case NonFatal(e) => ctx => {
      log.error(e, InternalServerError.defaultMessage)
      ctx.complete(InternalServerError)
    }
  }

  implicit val rejectionHandler = NDRejectionHandler

  def receive: Receive =
    runRoute(route)(handler, rejectionHandler, context, RoutingSettings.default, LoggingContext.fromActorRefFactory)
}
