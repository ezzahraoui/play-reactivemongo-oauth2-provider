package models

import org.joda.time.DateTime
import java.util.UUID
import play.api.libs.json.Json

case class OAuthAuthorizationCode(
  _id: UUID,
  accountId: UUID,
  account: Option[Account] = None,
  oauthClientId: UUID,
  oauthClient: Option[OAuthClient] = None,
  code: String,
  redirectUri: Option[String],
  createdAt: DateTime)

object OAuthAuthorizationCode {
  implicit val jsonFormat = Json.format[OAuthAuthorizationCode]
}

