package controllers

import javax.inject._
import play.api.mvc.{ Action, Controller }
import play.api.libs.json.{ Json, Writes }
import models.Account
import handlers.MongoDataHandler

import scala.concurrent.Future
import scalaoauth2.provider._
import scalaoauth2.provider.OAuth2ProviderActionBuilders._

@Singleton
class OAuthController @Inject() (mongoDataHandler: MongoDataHandler) extends Controller with OAuth2Provider {

  implicit val authInfoWrites = new Writes[AuthInfo[Account]] {
    def writes(authInfo: AuthInfo[Account]) = {
      Json.obj(
        "account" -> Json.obj(
          "email" -> authInfo.user.email),
        "clientId" -> authInfo.clientId,
        "redirectUri" -> authInfo.redirectUri)
    }
  }

  override val tokenEndpoint = new TokenEndpoint {
    override val handlers = Map(
      OAuthGrantType.AUTHORIZATION_CODE -> new AuthorizationCode(),
      OAuthGrantType.REFRESH_TOKEN -> new RefreshToken(),
      OAuthGrantType.CLIENT_CREDENTIALS -> new ClientCredentials(),
      OAuthGrantType.PASSWORD -> new Password())
  }

  def accessToken = Action.async { implicit request =>
    issueAccessToken(mongoDataHandler)
  }

  def resources = AuthorizedAction(mongoDataHandler) { request =>
    Ok(Json.toJson(request.authInfo))
  }

}
