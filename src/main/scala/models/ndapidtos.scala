/**
 * Created by nikolatonkev on 2014-05-20.
 */
package models.ndapidtos

import java.util.UUID
import org.joda.time.DateTime

case class DeviceRegisterModel (
  email: String,
  deviceUniqueId: String
)

case class UserRegisterDTO (
  //userid: UUID,
  username: String,
  email: Option[String],
  token: Option[String],
  tokenexpirydate: Option[DateTime],
  Id: Option[Int],
  verifiedemail: Option[Boolean],
  givenname: Option[String],
  surname: Option[String],
  link: Option[String],
  picture: Option[String],
  gender: Option[Int],
  applicationid: Option[UUID]

  //userPassword: String,
  //secretQuestion: Option[String] = None,
  //secretAnswer: Option[String] = None
)

//applicationId: UUID
