package controllers.gsn

import javax.inject.Singleton

import scalaoauth2.provider._

@Singleton
class OAuth2Endpoint extends TokenEndpoint {
  override val handlers = Map(
    OAuthGrantType.AUTHORIZATION_CODE -> new AuthorizationCode(),
    OAuthGrantType.REFRESH_TOKEN -> new RefreshToken(),
    OAuthGrantType.CLIENT_CREDENTIALS -> new ClientCredentials(),
    //OAuthGrantType.IMPLICIT -> new Implicit(),
    OAuthGrantType.PASSWORD -> new Password()
  )
}