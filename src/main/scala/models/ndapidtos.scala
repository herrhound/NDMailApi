/**
 * Created by nikolatonkev on 2014-05-20.
 */
package models.ndapidtos

import java.util.UUID

case class DeviceRegisterModel (
  email: String,
  deviceUniqueId: String
)

case class RegisterModel (
  email: String,
  userName: String,
  userPassword: String,
  secretQuestion: Option[String] = None,
  secretAnswer: Option[String] = None
)

//applicationId: UUID
