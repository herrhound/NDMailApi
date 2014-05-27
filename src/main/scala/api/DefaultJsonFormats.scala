package api

import spray.httpx.marshalling.{Marshaller, CollectingMarshallingContext, MetaMarshallers}
import spray.httpx.SprayJsonSupport
import spray.json._
import scala.reflect.ClassTag
import java.util.UUID
import spray.http.StatusCode
import web.ErrorResponseException
import models.ErrorStatus

/**
 * Created by Nikola on 3/11/14.
 */
trait DefaultJsonFormats extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  def jsonObjectFormat[A : ClassTag]: RootJsonFormat[A] = new RootJsonFormat[A] {
    val ct = implicitly[ClassTag[A]]
    def write(obj: A): JsValue = JsObject("value" -> JsString(ct.runtimeClass.getSimpleName))
    def read(json: JsValue): A = ct.runtimeClass.newInstance().asInstanceOf[A]
  }

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Wrong UUID format, Expected UUID but got " + x)
    }
  }


  implicit object ErrorStatusFormater extends RootJsonFormat[ErrorStatus.ErrorStatus] {
    def write(obj: ErrorStatus.ErrorStatus): JsValue = JsString(obj.toString)

    def read(json: JsValue): ErrorStatus.ErrorStatus = json match {
      case JsString(str) => ErrorStatus.withName(str)
      case _ => throw new DeserializationException("Enum string expected")
    }
  }


  type ErrorSelector[A] = A => StatusCode

  implicit def errorSelectingEitherMarshaller[A, B](implicit ma: Marshaller[A], mb: Marshaller[B], esa: ErrorSelector[A]): Marshaller[Either[A, B]] =
    Marshaller[Either[A, B]] { (value, ctx) =>
      value match {
        case Left(a) =>
          val mc = new CollectingMarshallingContext()
          ma(a, mc)
          ctx.handleError(ErrorResponseException(esa(a), mc.entity))
        case Right(b) =>
          mb(b, ctx)
      }
    }
}
