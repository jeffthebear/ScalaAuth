package controllers

import javax.inject._
import play.api._
import play.api.mvc._

class RestrictedController @Inject() extends Controller {
  def index = Action { implicit request =>
    if (request.session.get("username") == None)
      Ok(views.html.unauthorized(""))
    else
      Ok(views.html.restricted(""))
  }
}
