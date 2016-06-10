package controllers

import javax.inject._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc._

import pdi.jwt._

import services.{AuthService, FormsService}

@Singleton
class AuthController @Inject() (formsService: FormsService,
                                authService: AuthService,
                                val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def login() = Action { request =>
    val loginForm = formsService.getLoginForm
    Ok(views.html.login(loginForm))
  }

  def doLogin() = Action { implicit request =>
    val messages = messagesApi.preferred(request)
    val loginForm = formsService.getLoginForm
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.login.render(formWithErrors, messages))
      },
      userData => {
        /* binding success, you get the actual value. */
        if (authService.authenticateUser(userData.username, userData.password)) {
          val jwtClaim = JwtClaim().by("me").to("you").about(userData.username).issuedNow.startsNow.expiresIn(300)
          Redirect(routes.HomeController.index).addingToJwtSession("user", userData.username).withSession("user" -> Jwt.encode(jwtClaim))
        } else {
          val formWithError = loginForm.withGlobalError("username or password incorrect")
          BadRequest(views.html.login.render(formWithError, messages))
        }
      }
    )
  }

  def logout() = Action { implicit request =>
    Redirect(routes.AuthController.login).withNewSession
  }
}
