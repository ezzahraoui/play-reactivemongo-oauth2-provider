package services

import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import models._
import scala.concurrent.Future
import org.joda.time.DateTime
import java.util.UUID
import java.security.SecureRandom
import scala.util.Random

import reactivemongo.api._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo._
import reactivemongo.play.json.collection.JSONCollection

class OAuthAccessTokenService @Inject() (val reactiveMongoApi: ReactiveMongoApi) {

  val collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("oauthAccessToken"))

  def create(account: Account, client: OAuthClient): Future[Option[OAuthAccessToken]] = {
    def randomString(length: Int) = new Random(new SecureRandom()).alphanumeric.take(length).mkString
    val generatedAccessToken = randomString(40)
    val generatedRefreshToken = randomString(40)
    val generatedUUID = UUID.randomUUID
    val generatedDate = new DateTime()
    val oAuthAccessToken = OAuthAccessToken(
      _id = generatedUUID,
      accountId = account._id,
      account = Option(account),
      oauthClientId = client._id,
      oauthClient = Option(client),
      accessToken = generatedAccessToken,
      refreshToken = generatedRefreshToken,
      createdAt = generatedDate)
    collection.flatMap(_.insert(oAuthAccessToken).map {
      case writeResult if writeResult.ok == true => Option(oAuthAccessToken)
      case writeResult => None
    })
  }

  def delete(account: Account, client: OAuthClient): Future[Boolean] = {
    collection.flatMap(_.remove(Json.obj("accountId" -> account._id, "oauthClientId" -> client._id)) map {
      case writeResult if writeResult.ok == true => true
      case writeResult => false
    })
  }

  def refresh(account: Account, client: OAuthClient): Future[Option[OAuthAccessToken]] = {
    delete(account, client)
    create(account, client)
  }

  def findByAccessToken(accessToken: String): Future[Option[OAuthAccessToken]] = {
    val query = Json.obj("accessToken" -> accessToken)
    collection.flatMap(_.find(query).one[OAuthAccessToken])
  }

  def findByAuthorized(account: Account, clientId: String): Future[Option[OAuthAccessToken]] = {
    val query = Json.obj("accountId" -> account._id, "oauthClientId" -> clientId)
    collection.flatMap(_.find(query).one[OAuthAccessToken])
  }

  def findByRefreshToken(refreshToken: String): Future[Option[OAuthAccessToken]] = {
    val expireAt = new DateTime().minusMonths(1)
    val query = Json.obj("refreshToken" -> refreshToken, "createdAt" -> Json.obj("$gt" -> expireAt))
    collection.flatMap(_.find(query).one[OAuthAccessToken])
  }
}