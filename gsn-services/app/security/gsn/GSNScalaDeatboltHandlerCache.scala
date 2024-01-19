package security.gsn

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import com.feth.play.module.pa.PlayAuthenticate
import javax.inject.Inject
import be.objectify.deadbolt.scala.ExecutionContextProvider

class GSNScalaDeadboltHandlerCache @Inject()(auth: PlayAuthenticate, execContextProvider:ExecutionContextProvider) extends HandlerCache {
  val defaultHandler: DeadboltHandler = new GSNScalaDeadboltHandler(auth,None)
  
  override def apply(): DeadboltHandler = defaultHandler
  override def apply(handlerKey: HandlerKey): DeadboltHandler = defaultHandler
}
