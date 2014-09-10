package models

import spray.httpx.Json4sSupport
import org.json4s.{DefaultFormats, Formats}

/**
 * Created by nikolatonkev on 2014-08-14.
 */

object GoogleJsonProtocol extends Json4sSupport {

  override implicit def json4sFormats: Formats = DefaultFormats

  case class GoogleToken(access_token: String, refresh_token: String, expires_in: Int, token_type: String)

  //case class GoogleUserInfo(username: String, email: String)

  case class GoogleTokenRequest(code: String, client_id: String, client_secret: String, redirect_uri: String,
                                grant_type: String)

}

