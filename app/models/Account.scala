package models

import org.joda.time.DateTime
import java.util.UUID
import play.api.libs.json.Json

case class Account(
  _id: UUID,
  email: String,
  password: String,
  createdAt: DateTime)

object Account {
  implicit val jsonFormat = Json.format[Account]
}

