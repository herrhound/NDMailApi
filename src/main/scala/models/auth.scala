package models.auth

import java.util.UUID
import org.joda.time._

case class User(userid: UUID, username: String, email: Option[String], token: Option[String],
                tokenexpirydate: Option[DateTime], Id: Option[Int], verifiedemail: Option[Boolean],
                givenname: Option[String], surname: Option[String], link: Option[String],
                picture: Option[String], gender: Option[Int],
                applicationid: Option[UUID])
/* secretquestion: Option[String], secretanswer: Option[String],userpassword: String, */

case class UserDevice (userdevicesid: Option[Int] = None, userid: UUID, deviceid: UUID, authguid: UUID, expiredate: Option[DateTime])

case class Device (deviceid: UUID, userid: UUID, deviceuniqueid: String, devicetype: Option[String])

case class UserInfo(id: String, family_name: Option[String], gender: Option[String], given_name: Option[String],
                 link: Option[String], locale: Option[String], name: Option[String], picture: Option[String])