package test

import akka.actor.ActorSystem
import org.scalatestplus.play.PlaySpec
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext
import controllers.gsn.OAuth2Controller
import controllers.gsn.auth.PermissionsController
import controllers.gsn.api.WebSocketForwarder
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.json._
import org.scalatest.BeforeAndAfterAll
import play.api.test.CSRFTokenHelper._
import play.api.Application
import akka.stream.scaladsl.Flow
import play.api.http.websocket.Message
import play.api.db.Database

class WebSocketForwarderTest extends PlaySpec with BeforeAndAfterAll{

  val actorSystem = ActorSystem("test")
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val app = new GuiceApplicationBuilder()
    .bindings(
      bind[ControllerComponents].to[DefaultControllerComponents]
    )
    .build()

  
  var db: Database = _
  var access_token: String = ""

  override def beforeAll(): Unit = {
    db = app.injector.instanceOf[Database];

    play.api.Play.start(app)

    val oauthcontroller = app.injector.instanceOf[OAuth2Controller]
    val oauth2request = FakeRequest("POST", "/oauth2/token")
    val requestData = Map(
      "grant_type" -> "client_credentials",
      "client_id" -> "web-gui-public",
      "client_secret" -> "web-gui-secret"
    )

    val futureAccessToken = oauthcontroller.accessToken()(oauth2request.withFormUrlEncodedBody(requestData.toSeq: _*))

    val tokenresult: Result = await(futureAccessToken)
    println(tokenresult)

    if (tokenresult.header.status == OK) {
      val json = Json.parse(contentAsString(futureAccessToken))
      access_token = (json \ "access_token").as[String]
    } else {
      throw new RuntimeException(s"Access token request failed with status: ${tokenresult.header.status}")
    }
  }
  override def afterAll(): Unit = {
    play.api.Play.stop(app)
  }

  "WebSocketForwarder" should{
    "return forbidden for no access" in {
      var forwarder = app.injector.instanceOf[WebSocketForwarder]
      var permissionsController = app.injector.instanceOf[PermissionsController]

      var request = FakeRequest("GET", s"/access/vs")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
      var futureResult = permissionsController.vs(1)(request)
      var result: Result = await(futureResult)
        status(futureResult) mustBe OK

      request = FakeRequest("POST", s"/access/vs/1/removefrom?vs_id=1&id=ur1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
      futureResult = permissionsController.removefromvs(1)(request)
      result= await(futureResult)
      println("result remove"+result)
      status(futureResult) mustBe OK 
    
      request = FakeRequest("GET", "/api/sensors/MultiFormatTemperatureHandler/stream")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
      var futureResultforwarder =  forwarder.socket("MultiFormatTemperatureHandler")(request)
      var resultforwarder= await(futureResultforwarder)
      resultforwarder match {
      case Left(forbiddenResult) =>
        forbiddenResult.header.status mustBe FORBIDDEN
      case Right(_) =>
        fail("Expected a Left result with Forbidden message, but got a Right result.")
      }
    }

    "return forbidden (access_token)" in {
      var forwarder = app.injector.instanceOf[WebSocketForwarder]
      var request = FakeRequest("GET", "/api/sensors/MultiFormatTemperatureHandler/stream")
                    .withHeaders("Authorization" -> s"Bearer $access_token")
      var futureResultforwarder =  forwarder.socket("MultiFormatTemperatureHandler")(request)
      var resultforwarder= await(futureResultforwarder)

      resultforwarder match {
      case Left(forbiddenResult) =>
        forbiddenResult.header.status mustBe FORBIDDEN

      case Right(_) =>
        fail("Expected a Left result with Forbidden message, but got a Right result.")
      }
    }
    "test forwarding" in {
      var forwarder = app.injector.instanceOf[WebSocketForwarder]
      var permissionsController = app.injector.instanceOf[PermissionsController]

      val requestadd = FakeRequest("POST", s"/access/vs/1/addto?vs_id=1&id=ur1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost").withCSRFToken
      val futureResultadd = permissionsController.addtovs(1)(requestadd)
        var result3: Result = await(futureResultadd)
        status(futureResultadd) mustBe OK 


      var request = FakeRequest("GET", "/api/sensors/MultiFormatTemperatureHandler/stream")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
      var futureResult =  forwarder.socket("MultiFormatTemperatureHandler")(request)
      var result= await(futureResult)
      result match {
        case Left(forbiddenResult) =>
          fail(s"Expected a Right result with Flow, but got a Left result: $forbiddenResult")

        case Right(flow) =>
          flow.getClass mustBe classOf[Flow[Message, Message, _]]
      }

      request = FakeRequest("GET", "/api/sensors/MultiFormatTemperatureHandler/stream")
                    .withHeaders("Authorization" -> s"Bearer $access_token")
      futureResult =  forwarder.socket("MultiFormatTemperatureHandler")(request)
      result= await(futureResult)
      result match {
        case Left(forbiddenResult) =>
          fail(s"Expected a Right result with Flow, but got a Left result: $forbiddenResult")

        case Right(flow) =>
          flow.getClass mustBe classOf[Flow[Message, Message, _]]
      }

      
      val requestdel = FakeRequest("POST", s"/access/vs/1/removefrom?vs_id=1&id=ur1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost").withCSRFToken
      val futureResultdel = permissionsController.removefromvs(1)(requestdel)
        result3= await(futureResultdel)
        status(futureResultdel) mustBe OK   
    }
  }
}
