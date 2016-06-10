package services

import models.User

class UserService {
  def findUser(username: String): User = {
    User(username)
  }
}
