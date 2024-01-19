package test

import akka.actor.ActorSystem
import org.scalatestplus.play.PlaySpec
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext
import controllers.gsn.api.DataProcessService
import controllers.gsn.OAuth2Controller
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.json._
import org.scalatest.BeforeAndAfterAll
import play.api.test.CSRFTokenHelper._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.db.evolutions._
import play.api.db.Database
import java.time.LocalDate



class DataProcessServiceTest extends PlaySpec with BeforeAndAfterAll{

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

  "DataProcessService" should {
    "process data in different settings" in {
      val to= LocalDate.now()
      val from= to.minusDays(1)

      val controller = app.injector.instanceOf[DataProcessService]

      var request = FakeRequest("GET", s"/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=wma&params=10&format=json&from=${from}&to=${to}")
      var result = controller.processData("MultiFormatTemperatureHandler", "light")(request)
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")

      val requestcsv = FakeRequest("GET", "/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=wma&params=10&format=csv")
      val resultcsv = controller.processData("MultiFormatTemperatureHandler", "light")(requestcsv)
      status(resultcsv) mustBe OK
      contentType(resultcsv) mustBe Some("text/plain")

      val requestxml = FakeRequest("GET", "/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=wma&params=10&format=xml")
      val resultxml = controller.processData("MultiFormatTemperatureHandler", "light")(requestxml)
      status(resultxml) mustBe OK
      contentType(resultxml) mustBe Some("text/plain")
    }

    "return specific response format for processData method" in {
        val controller = app.injector.instanceOf[DataProcessService]
        var request = FakeRequest("GET", "/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=linear-interp&params=10&format=xml")
        var result = controller.processData("MultiFormatTemperatureHandler", "light")(request)
        status(result) mustBe OK
        contentType(result) mustBe Some("text/plain")

        request = FakeRequest("GET", "/api/data?sensorid=MultiFormatTemperatureHandler&fieldid=light&op=wma&params=10")
        result = controller.processData("MultiFormatTemperatureHandler", "light")(request)
        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
    }

    "return BadRequest for wrong sensor" in {
        val controller = app.injector.instanceOf[DataProcessService]
        var request = FakeRequest("GET", "/api/data?sensorid=wrong_sensor_id&fieldid=1&op=wma&params=10&format=json")
        var result = controller.processData("wrong_sensor_id", "wrong_field_id")(request)
        status(result) mustBe BAD_REQUEST
        contentAsString(result) must include("Sensor id wrong_sensor_id is not valid.")
    }


  }

}
