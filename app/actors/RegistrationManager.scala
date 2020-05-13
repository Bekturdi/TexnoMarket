package actors

import akka.actor.Actor
import akka.pattern.pipe
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import dao.UserDao
import javax.inject.Inject
import play.api.{Configuration, Environment}
import protocols.AdminProtocol.{CreateUser, LoginUser, User, loginUser}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class RegistrationManager @Inject()(val environment: Environment,
                                    val configuration: Configuration,
                                    val userDao: UserDao
                                   )
                                   (implicit val ec: ExecutionContext)
  extends Actor with LazyLogging {
  val config: Configuration = configuration.get[Configuration]("server")

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {

    case CreateUser(data) =>
      createUser(data).pipeTo(sender())

//    case LoginUser(data) =>
//      loginUser(data).pipeTo(sender())
//
    case _ => logger.info(s"received unknown message")
  }

  private def createUser(data: User): Future[Either[String, String]] = {
    (for {
      response <- userDao.findUser(data.username)
    } yield response match {
      case Some(count) =>
        Left(count.username + " Bunday foydalanuvchi ro`yhatdan o`tgan!")
      case None =>
        userDao.createUser(data)
        Right(data.username + " nomli foydalanuvchi ro`yhatdan muvoffaqiyatli o`tdi!")
    })
  }
//
//  private def loginUser(data: loginUser): Future[Either[String, String]] = {
//    (for {
//      response <- userDao.findLoginUser(data.username)
//    } yield response match {
//      case Some(count) =>
//        Left(count.username + " foydalanuvchi nomi bilan password ni to`g`ri kiriting!")
//      case None =>
//        Right(data.username + "asda")
//    })
//  }
}
