package services

import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import java.security.MessageDigest
import models.Account
import scala.concurrent.Future
import org.joda.time.DateTime
import java.util.UUID

import reactivemongo.api._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo._
import reactivemongo.play.json.collection.JSONCollection

class AccountService @Inject() (val reactiveMongoApi: ReactiveMongoApi) {

  val collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("account"))

  private def digestString(s: String): String = {
    val md = MessageDigest.getInstance("SHA-1")
    md.update(s.getBytes)
    md.digest.foldLeft("") { (s, b) =>
      s + "%02x".format(if (b < 0) b + 256 else b)
    }
  }

  def authenticate(email: String, password: String): Future[Option[Account]] = {
    val hashedPassword = digestString(password)
    val query = Json.obj("email" -> email, "password" -> hashedPassword)
    collection.flatMap(_.find(query).one[Account])
  }

  def findByEmail(email: String): Future[Option[Account]] = {
    val query = Json.obj("email" -> email)
    collection.flatMap(_.find(query).one[Account])
  }

}