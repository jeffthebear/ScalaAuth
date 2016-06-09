package services

import play.api.data._
import play.api.data.Forms._

class FormsService {
  case class LoginData (username: String, password: String)

  def getLoginForm: Form[LoginData] = {
    Form(
      mapping(
        "username" -> nonEmptyText,
        "password" -> nonEmptyText
      )
      (LoginData.apply _)(LoginData.unapply _)
    )
  }
}
