package actors

import akka.actor.Actor
import akka.pattern.pipe
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import dao._
import javax.inject.Inject
import play.api.{Configuration, Environment}
import protocols.AdminProtocol.{AddPhone, CreateUser, DeletePhone, GetPhone, GetUser, LoginUser, LoginUserR, Phone, UpdatePhone, User}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class RegistrationManager @Inject()(val environment: Environment,
                                    val configuration: Configuration,
                                    val userDao: UserDao,
                                   val phoneDao: PhoneDao
                                   )
                                   (implicit val ec: ExecutionContext)
  extends Actor with LazyLogging {
  val config: Configuration = configuration.get[Configuration]("server")

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {

    case CreateUser(data) =>
      createUser(data).pipeTo(sender())

    case LoginUserR(data) =>
      loginUser(data).pipeTo(sender())

    case AddPhone(data) =>
      addPhone(data).pipeTo(sender())

    case GetPhone =>
      getPhoneList.pipeTo(sender())

    case GetUser =>
      getUserList.pipeTo(sender())

    case UpdatePhone(data) =>
      updatePhone(data).pipeTo(sender())

    case DeletePhone(id) =>
      deletePhone(id).pipeTo(sender())

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

  private def loginUser(data: LoginUser): Future[Either[String, User]] = {
   userDao.findLoginUser(data).mapTo[Option[User]].map{ user =>
     if (user.isDefined) {
       Right(user.get)
     } else {
       Left(data.username + " foydalanuvchi nomi bilan password ni to`g`ri kiriting!")
     }
   }
  }

  private def addPhone(data: Phone): Future[Either[String, String]] = {
    (for {
      response <- phoneDao.findPhone(data.phoneName)
    } yield response match {
      case Some(count) =>
        Left(count.phoneName + " Bunday telefon ro`yhatga qo`shilgan!")
      case None =>
        phoneDao.addPhone(data)
        Right(data.phoneName + " nomli telefon ro`yhatga muvoffaqiyatli qo`shildi!")
    })
  }

  private def getPhoneList: Future[Seq[Phone]] = {
    phoneDao.getPhone
  }

  private def getUserList: Future[Seq[User]] = {
    userDao.getUser
  }

  private def updatePhone(data: Phone): Future[Int] = {
    phoneDao.updatePhone(data)
  }

  private def deletePhone(id: Int): Future[Int] = {
    phoneDao.deletePhone(id)
  }
}
