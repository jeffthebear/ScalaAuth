package core

import play.api.libs.json.{Format, Json}

case class ResponseToken(token: String) { }

object ResponseToken {
  implicit val jsonFormat : Format[ResponseToken] = Json.format[ResponseToken]
}
