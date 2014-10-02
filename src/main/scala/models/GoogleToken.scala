package models

import spray.httpx.Json4sSupport
import org.json4s.{DefaultFormats, Formats}

/**
 * Created by nikolatonkev on 2014-08-14.
 */

object GoogleJsonProtocol extends Json4sSupport {

  override implicit def json4sFormats: Formats = DefaultFormats

  case class GoogleToken(access_token: String, expires_in: Int, id_token: String, refresh_token: String, token_type: String)

  case class GoogleTokenRequest(grant_type: String, code: String, client_id: String, client_secret: String, redirect_uri: String)

  case class GoogleUserInfo(family_name: String, gender: String, given_name: String, id: String, link: String,
                            locale: String, name: String, picture: String)

  case class GoogleRefreshTokenRequest(refresh_token: String, client_id: String, client_secret: String, grant_type: String)

  case class GoogleRefreshTokenResponse(access_token: String, expires_in: Int, token_type: String)
}