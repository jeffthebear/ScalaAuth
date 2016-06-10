package services

import javax.inject.Inject

import com.mongodb.casbah.Imports._

class AuthService @Inject() (configuration: play.api.Configuration) {
  // Use a Connection String
  val mongodbUri = configuration.underlying.getString("mongodb.uri")
  val mongodbDatabase = configuration.underlying.getString("mongodb.database")
  val mongodbUsersCollection = configuration.underlying.getString("mongodb.users_collection")

  val mongoClient: MongoClient = MongoClient(mongodbUri)
  val database = mongoClient(mongodbDatabase)

  def authenticateUser(username: String, password: String) : Boolean = {
    val usersCollection = database(mongodbUsersCollection)
    usersCollection.find(MongoDBObject("username" -> username)).length == 1
  }
}
