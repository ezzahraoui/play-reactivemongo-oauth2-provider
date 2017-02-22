package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import org.joda.time.DateTime

@Singleton
class HomeController @Inject() extends Controller {

  def index = Action {
    Ok(Json.obj("name" -> "play2 reactivemongo oauth2 provider", "version" -> "1.0")).as("application/json")
  }

}
