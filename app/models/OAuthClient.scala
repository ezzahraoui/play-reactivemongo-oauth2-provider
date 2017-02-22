package models

import org.joda.time.DateTime
import java.util.UUID
import play.api.libs.json.Json

case class OAuthClient(
  _id: UUID,
  ownerId: UUID,
  owner: Option[Account] = None,
  grantType: String,
  clientId: String,
  clientSecret: String,
  redirectUri: Option[String],
  createdAt: DateTime)

object OAuthClient {
  implicit val jsonFormat = Json.format[OAuthClient]
}