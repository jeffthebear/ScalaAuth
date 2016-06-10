package controllers

import javax.inject._
import be.objectify.deadbolt.scala.ActionBuilders
import play.api._
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class RestrictedController @Inject() (actionBuilder: ActionBuilders) extends Controller {
  def index = actionBuilder.RestrictAction("admin").defaultHandler() { implicit request =>
    Future {
      Ok(views.html.restricted(""))
    }
  }

  def unauthorized = Action {
    Ok(views.html.unauthorized(""))
  }
}
