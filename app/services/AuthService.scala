package services

class AuthService {
  def authenticateUser(username: String, password: String) : Boolean = {
    username == "admin" && password == "password"
  }
}
