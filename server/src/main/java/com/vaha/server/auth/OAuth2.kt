package com.vaha.server.auth

object OAuth2 {
  val OAUTH_RESPONSE_TYPE = "response_type"
  val OAUTH_CLIENT_ID = "client_id"
  val OAUTH_CLIENT_SECRET = "client_secret"
  val OAUTH_REDIRECT_URI = "redirect_uri"
  val OAUTH_USERNAME = "username"
  val OAUTH_PASSWORD = "password"
  val OAUTH_ASSERTION_TYPE = "assertion_type"
  val OAUTH_ASSERTION = "assertion"
  val OAUTH_SCOPE = "scope"
  val OAUTH_STATE = "state"
  val OAUTH_GRANT_TYPE = "grant_type"
  val OAUTH_HEADER_NAME = "Bearer"
  val OAUTH_CODE = "code"
  val OAUTH_ACCESS_TOKEN = "access_token"
  val OAUTH_EXPIRES_IN = "expires_in"
  val OAUTH_REFRESH_TOKEN = "refresh_token"
  val OAUTH_TOKEN_TYPE = "token_type"
  val OAUTH_TOKEN = "oauth_token"
  val OAUTH_TOKEN_DRAFT_0 = "access_token"
  val OAUTH_BEARER_TOKEN = "access_token"
  val OAUTH_VERSION_DIFFER = "oauth_signature_method"
  val ASSERTION = "assertion"

  enum class GrantType(val grantType: String) {
    AUTHORIZATION_CODE("authorization_code"), PASSWORD("password"), REFRESH_TOKEN("refresh_token");

    override fun toString(): String {
      return grantType
    }
  }

  object ContentType {
    val URL_ENCODED = "application/x-www-form-urlencoded"
    val JSON = "application/json"
  }

  object HeaderType {
    val CONTENT_TYPE = "Content-Type"
    val WWW_AUTHENTICATE = "WWW-Authenticate"
    val AUTHORIZATION = "Authorization"
    val PROVIDER = "X-Provider"
  }
}
