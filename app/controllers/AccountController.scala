package controllers

import be.objectify.deadbolt.java.actions.Group
import be.objectify.deadbolt.java.actions.Restrict
import be.objectify.deadbolt.java.actions.SubjectPresent
import com.feth.play.module.pa.PlayAuthenticate
import com.feth.play.module.pa.user.AuthUser
import models.User
import play.data.Form
import play.data.FormFactory
import play.data.format.Formats.NonEmpty
import play.data.validation.Constraints.MinLength
import play.data.validation.Constraints.Required
import play.i18n.Messages
import play.i18n.MessagesApi
import play.mvc.Controller
import play.mvc.Result
import providers.MyUsernamePasswordAuthProvider
import providers.MyUsernamePasswordAuthUser
import service.UserProvider
import javax.inject.Inject
import play.api._
import play.api.mvc._

class Accept() {
  @Required
  @NonEmpty
  var _accept: Boolean

  def getAccept: Boolean = {
    return _accept
  }

  def setAccept(accept: Boolean) {
    _accept = accept
  }
}

class PasswordChange() {
  @MinLength(5)
  @Required var password: String
  @MinLength(5)
  @Required var repeatPassword: String

  def getPassword: String = {
    return password
  }

  def setPassword(toPassword: String) {
    password = toPassword
  }

  def getRepeatPassword: String = {
    return repeatPassword
  }

  def setRepeatPassword(toRepeatPassword: String) {
    repeatPassword = toRepeatPassword
  }

  def validate: String = {
    if (password == null || !(password == repeatPassword)) {
      return Messages.get("playauthenticate.change_password.error.passwords_not_same")
    }
    return null
  }
}

class AccountController @Inject() (auth: PlayAuthenticate, userProvider: UserProvider, myUsrPaswProvider: MyUsernamePasswordAuthProvider, formFactory: FormFactory, msg: MessagesApi) extends Controller {
  val ACCEPT_FORM = formFactory.form(classOf[Accept])
  val PASSWORD_CHANGE_FORM = formFactory.form(classOf[PasswordChange])

  @SubjectPresent def link: Result = {
    //com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    return Ok(link.render(userProvider, auth))
  }

  @Restrict(Array(new Group(Array(Application.USER_ROLE)))) def verifyEmail: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val user: User = userProvider.getUser(session)
    if (user.emailValidated) {
      flash(Application.FLASH_MESSAGE_KEY, msg.preferred(request).at("playauthenticate.verify_email.error.already_validated"))
    }
    else if (user.email != null && !user.email.trim.isEmpty) {
      flash(Application.FLASH_MESSAGE_KEY, msg.preferred(request).at("playauthenticate.verify_email.message.instructions_sent", user.email))
      myUsrPaswProvider.sendVerifyEmailMailingAfterSignup(user, ctx)
    }
    else {
      flash(Application.FLASH_MESSAGE_KEY, msg.preferred(request).at("playauthenticate.verify_email.error.set_email_first", user.email))
    }
    return redirect(routes.Application.profile)
  }

  @Restrict(Array(new Group(Array(Application.USER_ROLE)))) def changePassword: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val u: User = userProvider.getUser(session)
    if (!u.emailValidated) {
      return ok(unverified.render(userProvider))
    }
    else {
      return ok(password_change.render(userProvider, PASSWORD_CHANGE_FORM))
    }
  }

  @Restrict(Array(new Group(Array(Application.USER_ROLE)))) def doChangePassword: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val filledForm: Form[Account.PasswordChange] = PASSWORD_CHANGE_FORM.bindFromRequest
    if (filledForm.hasErrors) {
      return badRequest(password_change.render(userProvider, filledForm))
    }
    else {
      val user: User = userProvider.getUser(session)
      val newPassword: String = filledForm.get.password
      user.changePassword(new MyUsernamePasswordAuthUser(newPassword), true)
      flash(Application.FLASH_MESSAGE_KEY, msg.preferred(request).at("playauthenticate.change_password.success"))
      return redirect(routes.Application.profile)
    }
  }

  @SubjectPresent def askLink: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val u: AuthUser = auth.getLinkUser(session)
    if (u == null) {
      return redirect(routes.Application.index)
    }
    return ok(ask_link.render(userProvider, ACCEPT_FORM, u))
  }

  @SubjectPresent def doLink: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val u: AuthUser = auth.getLinkUser(session)
    if (u == null) {
      return redirect(routes.Application.index)
    }
    val filledForm: Form[Account.Accept] = ACCEPT_FORM.bindFromRequest
    if (filledForm.hasErrors) {
      return badRequest(ask_link.render(userProvider, filledForm, u))
    }
    else {
      val link: Boolean = filledForm.get.accept
      if (link) {
        flash(Application.FLASH_MESSAGE_KEY, msg.preferred(request).at("playauthenticate.accounts.link.success"))
      }
      return auth.link(ctx, link)
    }
  }

  @SubjectPresent def askMerge: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val aUser: AuthUser = auth.getUser(session)
    val bUser: AuthUser = auth.getMergeUser(session)
    if (bUser == null) {
      return redirect(routes.Application.index)
    }
    return ok(ask_merge.render(userProvider, ACCEPT_FORM, aUser, bUser))
  }

  @SubjectPresent def doMerge: Result = {
    com.feth.play.module.pa.controllers.Authenticate.noCache(response)
    val aUser: AuthUser = auth.getUser(session)
    val bUser: AuthUser = auth.getMergeUser(session)
    if (bUser == null) {
      return redirect(routes.Application.index)
    }
    val filledForm: Form[Account.Accept] = ACCEPT_FORM.bindFromRequest
    if (filledForm.hasErrors) {
      return badRequest(ask_merge.render(userProvider, filledForm, aUser, bUser))
    }
    else {
      val merge: Boolean = filledForm.get.accept
      if (merge) {
        flash(Application.FLASH_MESSAGE_KEY, msg.preferred(request).at("playauthenticate.accounts.merge.success"))
      }
      return auth.merge(ctx, merge)
    }
  }
}
