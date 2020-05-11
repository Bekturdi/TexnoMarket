package dao

import akka.actor.ActorSystem
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.AdminProtocol._
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import scala.concurrent.{ExecutionContext, Future}

trait UserDaoComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class UserTable(tag: Tag) extends Table[User](tag, "User") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def username = column[String]("username")

    def password = column[String]("password")

    def email = column[String]("email")

    def * = (id.?, username, password, email) <> (User.tupled, User.unapply _)
  }

}

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {
  def createUser(data: User): Future[Int]

  def findUser(user: String): Future[Option[User]]
}

@Singleton
class UserDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                               val actorSystem: ActorSystem)
                              (implicit val ec: ExecutionContext)
  extends UserDao
    with UserDaoComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with Date2SqlDate
    with LazyLogging {

  import utils.PostgresDriver.api._

  val userTable = TableQuery[UserTable]

  override def createUser(data: User): Future[Int] = {
    db.run {
      (userTable returning userTable.map(_.id)) += data
    }
  }

  override def findUser(user: String): Future[Option[User]] = {
    db.run{
      userTable.filter(data => data.username === user).result.headOption
    }
  }
}

