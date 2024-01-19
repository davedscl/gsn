package ch.epfl.gsn.data

import akka.testkit._
import ch.epfl.gsn.data.QueryActor;
import akka.actor._
//import org.scalatest.matchers.ShouldMatchers
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.Promise
import org.scalatest.wordspec.AnyWordSpecLike

class TestQueryActor extends TestKit(ActorSystem("test")) with ImplicitSender with AnyWordSpecLike with Matchers with BeforeAndAfterAll {


  //override def afterAll() { system.shutdown() }
  override def afterAll() { TestKit.shutdownActorSystem(system) }
  
  "MyTrait is called when triggered" must {
    
    "get " in{
  //  val x = TestProbe()
    val p=Promise[Seq[SensorData]]
    val a= TestActorRef(new QueryActor(p))
    a ! GetAllSensors
    implicit val ec=system.dispatcher
    p.future.onComplete{f=>
      f.get.size shouldBe(3)
      
    }
    
    
  }}
}