package models

import java.util.UUID
import models.ErrorStatus.ErrorStatus

object ErrorStatus extends Enumeration {
  type ErrorStatus = Value
  val None, NotAuthenticated, NotAutorized, BadAuthGuidFormat, MissingAuthGuid, MissingDevUniqueId, AuthExpired, ErrorGettingData = Value
}
import ErrorStatus._

case class NDApiRequest[G] (
  AuthGuyd: UUID,
  DeviceUniqueId: UUID,
  data: G
)

case class NDApiResponse[G] (
  ErrorStatus: ErrorStatus,
  ErrorMessage: String,
  data: G
)
