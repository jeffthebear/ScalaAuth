package models

import be.objectify.deadbolt.scala.models.Role

object AdminRole extends Role {
  override def name: String = "admin"
}
