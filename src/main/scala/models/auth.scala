package models.auth

import java.util.UUID

case class User(userid: UUID, username: String, userpassword: String, email: Option[String], secretquestion: Option[String], secretanswer: Option[String], transactionid: Option[Int], systemstatusid: Int)

