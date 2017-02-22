package models

import org.joda.time.DateTime
import java.util.UUID
import play.api.libs.json.Json

case class OAuthAccessToken(
  _id: UUID,
  accountId: UUID,
  account: Option[Account] = None,
  oauthClientId: UUID,
  oauthClient: Option[OAuthClient] = None,
  accessToken: String,
  refreshToken: String,
  createdAt: DateTime)

object OAuthAccessToken {
  implicit val jsonFormat = Json.format[OAuthAccessToken]
}
