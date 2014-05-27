package utils

import org.slf4j.LoggerFactory

/**
 * Created by nikolatonkev on 2014-05-21.
 */
trait NDApiLogging {
  implicit def authLogger = LoggerFactory.getLogger("auth-rolling")
  implicit def errorLogger = LoggerFactory.getLogger("error-rolling")
}
