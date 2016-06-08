package controllers

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class AuthController @Inject() extends Controller {

  def login() = Action {
    Ok(views.html.login("Login"))
  }

  def doLogin() = Action {
    Redirect(routes.HomeController.index)
  }

}
