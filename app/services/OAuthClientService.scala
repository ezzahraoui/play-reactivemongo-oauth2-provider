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

class OAuthClientService @Inject() (val reactiveMongoApi: ReactiveMongoApi) {

  val collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("oauthClient"))

  def validate(clientId: String, clientSecret: String, grantType: String): Future[Boolean] = {
    val query = Json.obj("clientId" -> clientId, "clientSecret" -> clientSecret)
    collection.flatMap(_.find(query).one[OAuthClient] map {
      case Some(oAuthClient) => grantType == oAuthClient.grantType || grantType == "refresh_token"
      case _ => false
    })
  }

  def findByClientId(clientId: String): Future[Option[OAuthClient]] = {
    val query = Json.obj("clientId" -> clientId)
    collection.flatMap(_.find(query).one[OAuthClient])
  }

  def findClientCredentials(clientId: String, clientSecret: String): Future[Option[Account]] = {
    val query = Json.obj("clientId" -> clientId, "clientSecret" -> clientSecret)
    collection.flatMap(_.find(query).one[OAuthClient] map {
      case Some(oauthClient) => oauthClient.owner
      case _ => None
    })
  }

}

