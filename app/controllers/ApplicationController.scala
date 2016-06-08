package controllers

import be.objectify.deadbolt.java.actions.Group
import be.objectify.deadbolt.java.actions.Restrict
import com.feth.play.module.pa.PlayAuthenticate
import models.User
import play.Routes
import play.data.Form
import play.mvc.Controller
import play.mvc.Result
import providers.MyUsernamePasswordAuthProvider
import providers.MyUsernamePasswordAuthProvider.MyLogin
import providers.MyUsernamePasswordAuthProvider.MySignup
import service.UserProvider
import views.html._
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class ApplicationController @Inject () (auth: PlayAuthenticate, provider: MyUsernamePasswordAuthProvider, userProvider: UserProvider) extends Controller {
  val FLASH_MESSAGE_KEY: String = "message"
  val FLASH_ERROR_KEY: String = "error"
  val USER_ROLE: String = "user"

  def formatTimestamp(t: Long): String = {
    return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t))
  }

  private final val auth: PlayAuthenticate = null
  private final val provider: MyUsernamePasswordAuthProvider = null
  private final val userProvider: UserProvider = null

  def index = Action {
    return Ok(views.html.index(userProvider))
  }

  @Restrict(Array(new Group(Array(Application.USER_ROLE)))) def restricted: Result = {
    val localUser: User = this.userProvider.getUser(session)
    return Ok(restricted.render(userProvider, localUser))
  }

  @Restrict(Array(new Group(Array(Application.USER_ROLE)))) def profile: Result = {
    val localUser: User = userProvider.getUser(session)
    return Ok(profile.render(auth, userProvider, localUser))
  }

  def login: Result = {
    return Ok(login.render(auth, userProvider, provider.getLoginForm))
  }

  def doLogin: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val filledForm: Form[MyUsernamePasswordAuthProvider.MyLogin] = provider.getLoginForm.bindFromRequest
    if (filledForm.hasErrors) {
      return badRequest(login.render(auth, userProvider, filledForm))
    }
    else {
      return provider.handleLogin(ctx)
    }
  }

  def signup: Result = {
    return Ok(signup.render(auth, userProvider, provider.getSignupForm))
  }

  def jsRoutes: Result = {
    return Ok(Routes.javascriptRouter("jsRoutes", controllers.routes.javascript.Signup.forgotPassword)).as("text/javascript")
  }

  def doSignup: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val filledForm: Form[MyUsernamePasswordAuthProvider.MySignup] = provider.getSignupForm.bindFromRequest
    if (filledForm.hasErrors) {
      return badRequest(signup.render(auth, userProvider, filledForm))
    }
    else {
      return provider.handleSignup(ctx)
    }
  }
}