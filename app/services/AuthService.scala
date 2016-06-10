package services

class AuthService {
  def authenticateUser(username: String, password: String) : Boolean = {
    password == "password"
  }
}
