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
import protocols.AdminProtocol.{AddPhone, CreateUser, GetPhone, GetUser, LoginUser, LoginUserR, Phone, UpdatePhone, User}
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

  val LoginSessionKey = "login"

  def index = Action {
    Ok(indexTemplate(Some("card")))
  }

  def loginForm = Action {
    Ok(loginTemplate())
  }

  def registration = Action {
    Ok(regTemplate())
  }

  def adminPage = Action { implicit request =>
    request.session.get(LoginSessionKey).map{_ =>
      Ok(adminTemplate(Some("")))
    }.getOrElse{
      Unauthorized
    }
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
    }.recover {
      case err =>
        logger.error(s"error: $err")
        BadRequest
    }
  }
  }

  def loginUsers: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val username = (request.body \ "username").as[String]
    val password = (request.body \ "password").as[String]
    (registrationManager ? LoginUserR(LoginUser(username, password))).mapTo[Either[String, User]].map {
      case Right(user) =>
        Ok(Json.toJson(user)).addingToSession(LoginSessionKey -> user.username)
      case Left(err) =>
        logger.error(s"error find user")
        BadRequest(err)
    }.recover {
      case err =>
        logger.error(s"error: $err")
        BadRequest
    }
  }
  }

  def addPhone: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val phoneName = (request.body \ "phoneName").as[String]
    val phoneModel = (request.body \ "phoneModel").as[String]
    val phoneRam = (request.body \ "phoneRam").as[String]
    val phoneHdd = (request.body \ "phoneHdd").as[String]
    val phonePrice = (request.body \ "phonePrice").as[String]
    (registrationManager ? AddPhone(Phone(None, phoneName, phoneModel, phoneRam, phoneHdd, phonePrice))).mapTo[Either[String, String]].map {
      case Right(str) =>
        Ok(Json.toJson(str))
      case Left(err) =>
        Ok(err)
    }.recover {
      case err =>
        logger.error(s"error: $err")
        BadRequest
    }
  }
  }

  def getPhoneList: Action[AnyContent] = Action.async {
    (registrationManager ? GetPhone).mapTo[Seq[Phone]].map { p =>
      Ok(Json.toJson(p))
    }.recover {
      case err =>
        logger.error(s"error: $err")
        BadRequest
    }
  }

  def getUserList: Action[AnyContent] = Action.async {
    (registrationManager ? GetUser).mapTo[Seq[User]].map { p =>
      Ok(Json.toJson(p))
    }.recover {
      case err =>
        logger.error(s"error: $err")
        BadRequest
    }
  }

  def updatePhone = Action.async(parse.json) { implicit request => {
    logger.error(s"sssss: ${request.body}")
    val id = (request.body \ "id").as[Int]
    val phoneName = (request.body \ "phoneName").as[String]
    val phoneModel = (request.body \ "phoneModel").as[String]
    val phoneRam = (request.body \ "phoneRam").as[String]
    val phoneHdd = (request.body \ "phoneHdd").as[String]
    val phonePrice = (request.body \ "phonePrice").as[String]
    (registrationManager ? UpdatePhone(Phone(Some(id), phoneName, phoneModel, phoneRam, phoneHdd, phonePrice))).mapTo[Int].map { i =>
      if (i != 0) {
        Ok(Json.toJson(id + " raqamli telefon ma`lumotlari yangilandi"))
      } else {
        Ok("Bunday raqamli telefon topilmadi")
      }
    }.recover {
      case err =>
        logger.error(s"error: $err")
        BadRequest
    }
  }
  }
}
