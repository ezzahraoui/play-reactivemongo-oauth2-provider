package services

import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import models._
import scala.concurrent.Future
import org.joda.time.DateTime
import java.util.UUID

import reactivemongo.api._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo._
import reactivemongo.play.json.collection.JSONCollection

class OAuthAuthorizationCodeService @Inject() (val reactiveMongoApi: ReactiveMongoApi) {

  val collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("oauthAuthorizationCode"))

  def findByCode(code: String): Future[Option[OAuthAuthorizationCode]] = {
    val expireAt = new DateTime().minusMinutes(30)
    val query = Json.obj("code" -> code, "createdAt" -> Json.obj("$gt" -> expireAt))
    collection.flatMap(_.find(query).one[OAuthAuthorizationCode])
  }

  def delete(code: String): Future[Unit] = {
    collection.flatMap(_.remove(Json.obj("code" -> code)))
    Future.successful(Unit)
  }

}

