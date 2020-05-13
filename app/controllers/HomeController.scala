package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.webjars.play.WebJarsUtil
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import protocols.AdminProtocol.{CreateUser, LoginUser, User, loginUser}
import views.html._
import views.html.admin.admin

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val configuration: Configuration,
                               implicit val webJarsUtil: WebJarsUtil,
                               @Named("registration-manager") val registrationManager: ActorRef,
                               indexTemplate: index,
                               loginTemplate: loginForm,
                               regTemplate: registrationForm,
                               adminTemplate: admin,
                              )
                              (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def index = Action {
    Ok(indexTemplate(Some("card")))
  }

  def loginForm = Action {
    Ok(loginTemplate())
  }

  def registration = Action {
    Ok(regTemplate())
  }

  def adminPage = Action {
    Ok(adminTemplate(Some("")))
  }

  def createUser: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val username = (request.body \ "username").as[String]
    val password = (request.body \ "password").as[String]
    val email = (request.body \ "email").as[String]
    (registrationManager ? CreateUser(User(None, username, password, email))).mapTo[Either[String, String]].map {
      case Right(str) =>
        Ok(Json.toJson(str))
      case Left(err) =>
        Ok(err)
    }
  }
  }

  def loginUsers: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val username = (request.body \ "username").as[String]
    val password = (request.body \ "password").as[String]
    (registrationManager ? LoginUser(loginUser(None, username, password))).mapTo[Either[String, String]].map {
      case Right(str) =>
        Ok(Json.toJson(str))
      case Left(err) =>
        Ok(err)
    }
  }
}
}
