package test

import akka.actor.ActorSystem
import org.scalatestplus.play.PlaySpec
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext
import controllers.gsn.api.DataProcessService
import controllers.gsn.api.SensorService
import controllers.gsn.OAuth2Controller
import controllers.gsn.auth.PermissionsController
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
import scala.concurrent.Future
import play.api.db.Database

class Oauth2ControllerTest extends PlaySpec with BeforeAndAfterAll{

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
  }
  override def afterAll(): Unit = {
    play.api.Play.stop(app)
  }

    "Oauth2Controller" should {
        "handle accessToken " in {
                var authController = app.injector.instanceOf[OAuth2Controller]
                val oauth2request = FakeRequest("POST", "/oauth2/token")
                val requestData = Map(
                    "grant_type" -> "password",
                    "username" -> "root@localhost",
                    "password" -> "changeme",
                    "client_id" -> "web-gui-public",
                    "client_secret" -> "web-gui-secret"
                )

                val futureAccessToken = authController.accessToken()(oauth2request.withFormUrlEncodedBody(requestData.toSeq: _*))

                val tokenresult: Result = await(futureAccessToken)

                if (tokenresult.header.status == OK) {
                    val json = Json.parse(contentAsString(futureAccessToken))
                    access_token = (json \ "access_token").as[String]
                    } else {
                    throw new RuntimeException(s"Access token request failed with status: ${tokenresult.header.status}")
                    }
                status(futureAccessToken) mustBe OK
                contentType(futureAccessToken) mustBe Some("application/json")
        }
        "handle auth" in{
            var authController = app.injector.instanceOf[OAuth2Controller]
            var request = FakeRequest("GET", s"/oauth2/auth")
                    .withHeaders("Authorization" -> s"Bearer $access_token")
            var result = authController.auth()(request)
            status(result) mustBe SEE_OTHER

            var redirectLocation = header("Location", result)
            redirectLocation mustBe Some("/login")

            val queryString = "?response_type=code&client_id=web-gui-public&client_secret=web-gui-secret"
            request = FakeRequest("GET", s"/oauth2/auth$queryString")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            var futureResult = authController.auth()(request)
            var result1 = await(futureResult)
            status(futureResult) mustBe BAD_REQUEST
        }

        "handle doAuth" in {
            var authController = app.injector.instanceOf[OAuth2Controller]
                            //handle POST request to doAuth
            var request = FakeRequest("POST", s"/oauth2/auth")
                    .withHeaders("Authorization" -> s"Bearer $access_token")

            var futureResult = authController.doAuth()(request)
            val result2: Result = await(futureResult)
            status(futureResult) mustBe SEE_OTHER
            var redirectLocation = header("Location", futureResult)
            redirectLocation mustBe Some("/login")

            request = FakeRequest("POST", "/oauth2/auth")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")

            futureResult = authController.doAuth()(request)
            val result3: Result = await(futureResult)
            status(futureResult) mustBe BAD_REQUEST

            val requestdoAuth = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "response_type" -> "code",  
                    "client_id" -> "web-gui-public",
                    "client_secret" -> "web-gui-secret",
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
            val futureResultdoAuth: Future[Result] = authController.doAuth()(requestdoAuth)
            val resultdoAuth: Result = await(futureResultdoAuth)
            status(futureResultdoAuth) mustBe 303 //redirect 

                
            val requestdoAuthforbidden = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "response_type" -> "code",  
                    "client_id" -> "web-gui-public",
                    "client_secret" -> "web-gui-public",
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
            val futureResultdoAuthforbidden: Future[Result] = authController.doAuth()(requestdoAuthforbidden)
            val resultdoAuthforbidden: Result = await(futureResultdoAuthforbidden)
            status(futureResultdoAuthforbidden) mustBe FORBIDDEN
        }

        "list clients" in {
            var authController = app.injector.instanceOf[OAuth2Controller]
                //list clients
                val requestclient = FakeRequest("GET", "/oauth2/client")
                            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                            .withCSRFToken
                val futureResultclient = authController.listClients()(requestclient)
                val resultclient = await(futureResultclient)
                status(futureResultclient) mustBe OK
        }


        "add a client" in {
            //add client
            var authController = app.injector.instanceOf[OAuth2Controller]
                val requestadd = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "0",
                    "action" -> "add",
                    "name" -> "testclient",
                    "client_id" -> "testclient_id",
                    "client_secret" -> "testclient_secret",
                    "redirect"->"http://xy.z",
                    "linked" -> "true"
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                val futureResultadd : Future[Result] = authController.editClient(requestadd)
                status(futureResultadd) mustBe OK
        }


        "edit an existing client" in {
            var authController = app.injector.instanceOf[OAuth2Controller]
                //edit existing client
                val requestedit = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "1",
                    "action" -> "edit",
                    "name" -> "Default Web UI",
                    "client_id" -> "web-gui-public",
                    "client_secret" -> "web-gui-secret",
                    "redirect"->"http://localhost:8000/profile/",
                    "linked" -> "true"
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                val futureResultedit : Future[Result] = authController.editClient(requestedit)
                status(futureResultedit) mustBe OK
        }


        "delete a client" in {
            var authController = app.injector.instanceOf[OAuth2Controller]
                val requestdel = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "99",
                    "action" -> "del",
                    "name" -> "testclient",
                    "client_id" -> "testclient_id",
                    "client_secret" -> "testclient_secret",
                    "redirect"->"http://xy.z",
                    "linked" -> "true"
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                var futureResultdel: Future[Result] = authController.editClient(requestdel)
                status(futureResultdel) mustBe 404 //not found

                //BAD REQUEST
                val badrequest = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "99",
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                val futureResultbad: Future[Result] = authController.editClient(badrequest)
                status(futureResultbad) mustBe BAD_REQUEST

                
                val requestsuccesfuldel = FakeRequest("POST", "/oauth2/client")
                    .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                    .withFormUrlEncodedBody(
                    "id" -> "1",
                    "action" -> "del",
                    "name" -> "testclient",
                    "client_id" -> "testclient_id",
                    "client_secret" -> "testclient_secret",
                    "redirect"->"http://xy.z",
                    "linked" -> "true"
                    ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
                    .withCSRFToken
                    
                futureResultdel = authController.editClient(requestsuccesfuldel)
                status(futureResultdel) mustBe OK

        }      

    }
}
