package deadbolt

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DynamicResourceHandler, DeadboltHandler}
import controllers.routes
import models.User
import pdi.jwt._
import play.api.mvc.{Results, Result, Request}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultDeadboltHandler extends DeadboltHandler {

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future {None}

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future {None}

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] =
    Future {
      request.subject.orElse {

        // TODO: get request.jwtSessionUser working
        val jwtSessionUser: Option[User] = None //request.jwtSession.getAs[User]("user")

        val queryStringToken = request.queryString.get("token") match {
          case Some(userToken) => if (userToken.isEmpty) None else Some(userToken.head)
          case _ => None
        }

        val sessionVarToken = request.session.get("user")

        print(JwtJson.decode(queryStringToken.getOrElse("")))
        val tokenList: List[Option[String]] = List(queryStringToken, sessionVarToken)

        val firstToken = tokenList.find(_.isDefined)

        if (jwtSessionUser.isDefined)
          jwtSessionUser
        else {
          firstToken match {
            case Some(userToken) => getUser(userToken.get)
            case _ => None
          }
        }

      }
    }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future {
      Results.Redirect(routes.RestrictedController.unauthorized)
    }
  }

  def getUser(tokenString: String) : Option[Subject] = {
    val decodedToken = JwtJson.decodeAll(tokenString)
    print("decodedToken")
    print(decodedToken)
    if (decodedToken.isFailure)
      return None
    val claim = decodedToken.get._2
    claim.subject match {
      case Some(subject) => Some(User(subject))
      case _ => None
    }
  }
}