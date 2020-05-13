package protocols

import play.api.libs.json.{Json, OFormat}

object AdminProtocol {

  case class CreateUser(data: User)

  case class LoginUser(data: loginUser)

  case class User(id: Option[Int] = None,
                  username: String,
                  password: String,
                  email: String)

  implicit val userFormat: OFormat[User] = Json.format[User]

  case class loginUser(id: Option[Int] = None,
                       username: String,
                       password: String)

  implicit val loginUserFormat: OFormat[loginUser] = Json.format[loginUser]
}
