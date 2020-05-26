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

trait PhoneDaoComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class PhoneTable(tag: Tag) extends Table[Phone](tag, "Phone") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def phoneName = column[String]("phoneName")

    def phoneModel = column[String]("phoneModel")

    def phoneRam = column[String]("phoneRam")

    def phoneHdd = column[String]("phoneHdd")

    def phonePrice = column[String]("phonePrice")

    def * = (id.?, phoneName, phoneModel, phoneRam, phoneHdd, phonePrice) <> (Phone.tupled, Phone.unapply _)
  }

}

@ImplementedBy(classOf[PhoneDaoImpl])
trait PhoneDao {
  def addPhone(data: Phone): Future[Int]

  def findPhone(name: String): Future[Option[Phone]]

  def getPhone: Future[Seq[Phone]]

  def updatePhone(data: Phone): Future[Int]

}

@Singleton
class PhoneDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                               val actorSystem: ActorSystem)
                              (implicit val ec: ExecutionContext)
  extends PhoneDao
    with PhoneDaoComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with Date2SqlDate
    with LazyLogging {

  import utils.PostgresDriver.api._

  val phoneTable = TableQuery[PhoneTable]

  override def addPhone(data: Phone): Future[Int] = {
    db.run {
      (phoneTable returning phoneTable.map(_.id)) += data
    }
  }

  override def findPhone(name: String): Future[Option[Phone]] = {
    db.run{
      phoneTable.filter(data => data.phoneName === name).result.headOption
    }
  }

  override def getPhone: Future[Seq[Phone]] = {
    db.run {
      phoneTable.result
    }
  }

  override def updatePhone(data: Phone): Future[Int] = {
    db.run{
      phoneTable.filter(_.id === data.id).update(data)
    }
  }

}

