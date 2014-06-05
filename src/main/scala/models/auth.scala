package models.auth

import java.util.UUID
import org.joda.time.DateTime

case class User(userid: UUID, username: String, userpassword: String, email: Option[String], secretquestion: Option[String], secretanswer: Option[String], transactionid: Option[Int], systemstatusid: Int)


case class UserDevice (userdevicesid: Option[Int] = None, userid: UUID, deviceid: UUID, authguid: UUID) //, expiredate: Option[DateTime]

