package controllers

import play.api.libs.json.Json
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok("Your new application is ready.")
  }

  def getTrucks = Action{
      MySQL.list match{
      case Left(e) => Ok(e)
      case Right(listOfTrucks) => Ok(Json.toJson(listOfTrucks))
    }
  }

}
