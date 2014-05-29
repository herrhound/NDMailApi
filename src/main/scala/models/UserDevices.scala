package models.auth

import java.util.UUID
import org.joda.time._

/**
 * Created by nikolatonkev on 2014-05-28.
 */
case class UserDevices (userdevicesid: Int, userid: UUID, deviceid: UUID, authguid: UUID, expiredate: DateTime)