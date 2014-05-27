package utils

import models.NDApiResponse
import java.util.UUID

/**
 * Created by nikolatonkev on 2014-05-21.
 */
trait NDApiUtil {
  implicit def GetNewUUID: UUID = java.util.UUID.randomUUID
  //implicit def GetNewUUID: String = java.util.UUID.randomUUID.toString
}