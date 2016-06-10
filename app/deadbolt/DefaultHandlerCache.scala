package deadbolt

import be.objectify.deadbolt.scala.{HandlerKey, DeadboltHandler}
import be.objectify.deadbolt.scala.cache.HandlerCache

//@Singleton
class DefaultHandlerCache extends HandlerCache {
  val defaultHandler: DeadboltHandler = new DefaultDeadboltHandler

  // HandlerKeys is an user-defined object, containing instances of a case class that extends HandlerKey
  val handlers: Map[Any, DeadboltHandler] = Map("defaultHandler" -> defaultHandler)

  // Get the default handler.
  override def apply(): DeadboltHandler = defaultHandler

  // Get a named handler
  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}
