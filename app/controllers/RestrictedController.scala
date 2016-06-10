package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import pdi.jwt._

class RestrictedController @Inject() extends Controller {
  def index = Action { implicit request =>
    def checkIfUserAuthorized(tokenString: String) : Boolean = {
      val decodedToken = JwtJson.decodeAll(tokenString)
      if (decodedToken.isFailure)
        return false
      val claim = decodedToken.get._2
      return claim.subject.getOrElse("") == "admin"
    }

    val isAuthorized = request.session.get("user") match {
      case Some(userToken) => checkIfUserAuthorized(userToken)
      case None => false
    }

    if (isAuthorized)
      Ok(views.html.restricted(""))
    else
      Ok(views.html.unauthorized(""))
  }
}
