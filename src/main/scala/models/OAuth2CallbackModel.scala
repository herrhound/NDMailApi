package models

/**
 * Created by nikolatonkev on 2014-08-13.
 */
case class OAuth2CallbackModel (
  code: String,
  client_id: String,
  client_secret: String,
  redirect_uri: String,
  grant_type: String
)
