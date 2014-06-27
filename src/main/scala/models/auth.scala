package models.auth

import java.util.UUID
import org.joda.time._

case class User(userid: UUID, username: String, userpassword: String, email: Option[String], secretquestion: Option[String], secretanswer: Option[String], applicationid: UUID)

case class UserDevice (userdevicesid: Option[Int] = None, userid: UUID, deviceid: UUID, authguid: UUID, expiredate: Option[DateTime])

case class Device (deviceid: UUID, userid: UUID, deviceuniqueid: String, devicetype: Option[String])

