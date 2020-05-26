package protocols

import play.api.libs.json.{Json, OFormat}

object AdminProtocol {

  case class CreateUser(data: User)

  case class LoginUserR(data: LoginUser)

  case class AddPhone(data: Phone)

  case class UpdatePhone(update: Phone)

  case object GetPhone

  case object GetUser

  case class User(id: Option[Int] = None,
                  username: String,
                  password: String,
                  email: String)

  implicit val userFormat: OFormat[User] = Json.format[User]

  case class LoginUser(username: String,
                       password: String)

  implicit val loginUserFormat: OFormat[LoginUser] = Json.format[LoginUser]

  case class Phone(id: Option[Int] = None,
                   phoneName: String,
                   phoneModel: String,
                   phoneRam: String,
                   phoneHdd: String,
                   phonePrice: String)

  implicit val phoneFormat: OFormat[Phone] = Json.format[Phone]
}
