package controllers

import controllers.AccountController
import models.TokenAction
import models.TokenAction.Type
import models.User
import play.data.Form
import play.data.FormFactory
import play.i18n.MessagesApi
import play.mvc.Controller
import play.mvc.Result
import providers.MyLoginUsernamePasswordAuthUser
import providers.MyUsernamePasswordAuthProvider
import providers.MyUsernamePasswordAuthProvider.MyIdentity
import providers.MyUsernamePasswordAuthUser
import service.UserProvider
import com.feth.play.module.pa.PlayAuthenticate
import javax.inject.Inject

class PasswordReset(token: String) extends PasswordChange {
  var _token = token

  def getToken: String = {
    return _token
  }

  def setToken(token: String) {
    _token = token
  }
}

class SignupController @Inject () (auth: PlayAuthenticate, userProvider: UserProvider, userPaswAuthProvider: MyUsernamePasswordAuthProvider, formFactory: FormFactory, msg: MessagesApi) extends Controller {
  private final val PASSWORD_RESET_FORM: Form[PasswordReset] = null
  private final val FORGOT_PASSWORD_FORM: Form[MyUsernamePasswordAuthProvider.MyIdentity] = null
  private final val auth: PlayAuthenticate = null
  private final val userProvider: UserProvider = null
  private final val userPaswAuthProvider: MyUsernamePasswordAuthProvider = null
  private final val msg: MessagesApi = null

  val PASSWORD_RESET_FORM = formFactory.form(classOf[PasswordReset])
  val FORGOT_PASSWORD_FORM = formFactory.form(classOf[MyUsernamePasswordAuthProvider.MyIdentity])

  def unverified: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    return Ok(views.account.unverified.render(this.userProvider))
  }

  def forgotPassword(email: String): Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    var form: Form[MyUsernamePasswordAuthProvider.MyIdentity] = FORGOT_PASSWORD_FORM
    if (email != null && !email.trim.isEmpty) {
      form = FORGOT_PASSWORD_FORM.fill(new MyUsernamePasswordAuthProvider.MyIdentity(email))
    }
    return Ok(password_forgot.render(this.userProvider, form))
  }

  def doForgotPassword: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val filledForm: Form[MyUsernamePasswordAuthProvider.MyIdentity] = FORGOT_PASSWORD_FORM.bindFromRequest
    if (filledForm.hasErrors) {
      return badRequest(password_forgot.render(this.userProvider, filledForm))
    }
    else {
      val email: String = filledForm.get.email
      flash(Application.FLASH_MESSAGE_KEY, this.msg.preferred(request).at("playauthenticate.reset_password.message.instructions_sent", email))
      val user: User = User.findByEmail(email)
      if (user != null) {
        val provider: MyUsernamePasswordAuthProvider = this.userPaswAuthProvider
        if (user.emailValidated) {
          provider.sendPasswordResetMailing(user, ctx)
        }
        else {
          flash(Application.FLASH_MESSAGE_KEY, this.msg.preferred(request).at("playauthenticate.reset_password.message.email_not_verified"))
          provider.sendVerifyEmailMailingAfterSignup(user, ctx)
        }
      }
      return redirect(routes.Application.index)
    }
  }

  /**
    * Returns a token object if valid, null if not
    *
    * @param token
    * @param type
    * @return
    */
  private def tokenIsValid(token: String, `type`: TokenAction.Type): TokenAction = {
    var ret: TokenAction = null
    if (token != null && !token.trim.isEmpty) {
      val ta: TokenAction = TokenAction.findByToken(token, `type`)
      if (ta != null && ta.isValid) {
        ret = ta
      }
    }
    return ret
  }

  def resetPassword(token: String): Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val ta: TokenAction = tokenIsValid(token, Type.PASSWORD_RESET)
    if (ta == null) {
      return badRequest(no_token_or_invalid.render(this.userProvider))
    }
    return ok(password_reset.render(this.userProvider, PASSWORD_RESET_FORM.fill(new Signup.PasswordReset(token))))
  }

  def doResetPassword: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val filledForm: Form[Signup.PasswordReset] = PASSWORD_RESET_FORM.bindFromRequest
    if (filledForm.hasErrors) {
      return badRequest(password_reset.render(this.userProvider, filledForm))
    }
    else {
      val token: String = filledForm.get.token
      val newPassword: String = filledForm.get.password
      val ta: TokenAction = tokenIsValid(token, Type.PASSWORD_RESET)
      if (ta == null) {
        return badRequest(no_token_or_invalid.render(this.userProvider))
      }
      val u: User = ta.targetUser
      try {
        u.resetPassword(new MyUsernamePasswordAuthUser(newPassword), false)
      }
      catch {
        case re: RuntimeException => {
          flash(Application.FLASH_MESSAGE_KEY, this.msg.preferred(request).at("playauthenticate.reset_password.message.no_password_account"))
        }
      }
      val login: Boolean = this.userPaswAuthProvider.isLoginAfterPasswordReset
      if (login) {
        flash(Application.FLASH_MESSAGE_KEY, this.msg.preferred(request).at("playauthenticate.reset_password.message.success.auto_login"))
        return this.auth.loginAndRedirect(ctx, new MyLoginUsernamePasswordAuthUser(u.email))
      }
      else {
        flash(Application.FLASH_MESSAGE_KEY, this.msg.preferred(request).at("playauthenticate.reset_password.message.success.manual_login"))
      }
      return redirect(routes.Application.login)
    }
  }

  def oAuthDenied(getProviderKey: String): Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    return Ok(oAuthDenied.render(this.userProvider, getProviderKey))
  }

  def exists: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    return Ok(exists.render(this.userProvider))
  }

  def verify(token: String): Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val ta: TokenAction = tokenIsValid(token, Type.EMAIL_VERIFICATION)
    if (ta == null) {
      return badRequest(no_token_or_invalid.render(this.userProvider))
    }
    val email: String = ta.targetUser.email
    User.verify(ta.targetUser)
    Flash(Application.FLASH_MESSAGE_KEY, this.msg.preferred(request).at("playauthenticate.verify_email.success", email))
    if (this.userProvider.getUser(session) != null) {
      return Redirect(routes.Application.index)
    }
    else {
      return Redirect(routes.Application.login)
    }
  }
}
