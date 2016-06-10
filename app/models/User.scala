package models

import be.objectify.deadbolt.scala.models.{Permission, Role, Subject}

case class User (username: String) extends Subject {
  override def identifier: String = username

  override def permissions: List[Permission] = List()

  override def roles: List[Role] = List(AdminRole)
}
