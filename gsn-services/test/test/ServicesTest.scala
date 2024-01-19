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
import controllers.gsn.api.GridService
import controllers.gsn.OAuth2Controller
import controllers.gsn.auth.PermissionsController
import security.gsn.GSNScalaDeadboltHandler
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
import models.gsn.data.GsnMetadata
import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.Database
import java.time.LocalDate

class ServicesTest extends PlaySpec with BeforeAndAfterAll{

  val actorSystem = ActorSystem("test")
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val app = new GuiceApplicationBuilder()
    .bindings(
      bind[ControllerComponents].to[DefaultControllerComponents]
    )
    .build()

  
  var db: Database = _
  var access_token: String = ""
  var refresh_token: String=""

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

    if (tokenresult.header.status == OK) {
      val json = Json.parse(contentAsString(futureAccessToken))
      access_token = (json \ "access_token").as[String]
      refresh_token = (json \ "refresh_token").as[String]
    } else {
      throw new RuntimeException(s"Access token request failed with status: ${tokenresult.header.status}")
    }
  }
  override def afterAll(): Unit = {
    play.api.Play.stop(app)
  }

      // PermissionController
    "PermissionsController" should{
      "show groups" in {
        var permissionsController = app.injector.instanceOf[PermissionsController]
        var request = FakeRequest("GET", s"/access/groups")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        var futureResult = permissionsController.groups(1)(request)
        var result: Result = await(futureResult)
        status(futureResult) mustBe OK
      }
      "add a group" in {
        var permissionsController = app.injector.instanceOf[PermissionsController]
        var request = FakeRequest("POST", "/access/groups/1/edit")
        .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
        .withFormUrlEncodedBody(
          "id" -> "0",
          "action" -> "add",
          "name" -> "test",
          "description" -> "description change"
        ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
        .withCSRFToken
        var futureResult = permissionsController.addgroup(1)(request)
        var result = await(futureResult)
        status(futureResult) mustBe OK
      }

      "edit a group" in {
        var permissionsController = app.injector.instanceOf[PermissionsController]
        var request = FakeRequest("POST", "/access/groups/1/edit")
        .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
        .withFormUrlEncodedBody(
          "id" -> "1",
          "action" -> "edit",
          "name" -> "test",
          "description" -> "description change"
        ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
        .withCSRFToken
        var futureResult = permissionsController.addgroup(1)(request)
        var result = await(futureResult)
        status(futureResult) mustBe OK


        request = FakeRequest("POST", "/access/groups/1/edit")
        .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
        .withFormUrlEncodedBody(
          "id" -> "0",
        ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
        .withCSRFToken
        futureResult = permissionsController.addgroup(1)(request)
        result = await(futureResult)
        status(futureResult) mustBe BAD_REQUEST
      }
       
      "add and remove a user" in {
        var permissionsController = app.injector.instanceOf[PermissionsController]
        var request = FakeRequest(POST, s"/access/groups/1/addto?user_id=1&group_id=1")
                .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                .withCSRFToken
        var futureResult = permissionsController.addtogroup(1)(request)
        var result = await(futureResult)
        status(futureResult) mustBe OK
        
        
        request = FakeRequest(POST, s"/access/groups/1/removefrom?user_id=1&group_id=1")
                .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
                .withCSRFToken
        futureResult = permissionsController.removefromgroup(1)(request)
        result = await(futureResult)
        status(futureResult) mustBe OK
      }
      
      "handle vs " in {
        var permissionsController = app.injector.instanceOf[PermissionsController]
        var request = FakeRequest("GET", s"/access/vs")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        var futureResult = permissionsController.vs(1)(request)
        var result: Result = await(futureResult)
        status(futureResult) mustBe OK
        
        request = FakeRequest("POST", s"/access/vs/1/addto?vs_id=3&id=ur1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.addtovs(1)(request)
        result = await(futureResult)
        status(futureResult) mustBe OK   

        request = FakeRequest("POST", s"/access/vs/1/addto?vs_id=1&id=ur1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.addtovs(1)(request)
        result = await(futureResult)

        status(futureResult) mustBe OK 
                request = FakeRequest("POST", s"/access/vs/1/addto?vs_id=1&id=uw1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.addtovs(1)(request)
        result = await(futureResult)
        status(futureResult) mustBe OK 

        request = FakeRequest("POST", s"/access/vs/1/addto?vs_id=3&id=uw1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.addtovs(1)(request)
        result= await(futureResult)
        status(futureResult) mustBe OK   

        request = FakeRequest("POST", s"/access/vs/1/addto?vs_id=3&id=gr1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.addtovs(1)(request)
        result= await(futureResult)
        status(futureResult) mustBe OK 

        request = FakeRequest("POST", s"/access/vs/1/addto?vs_id=3&id=gw1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.addtovs(1)(request)
        result= await(futureResult)
        status(futureResult) mustBe OK 

        request = FakeRequest("POST", s"/access/vs/1/removefrom?vs_id=3&id=ur1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.removefromvs(1)(request)
        result= await(futureResult)
        status(futureResult) mustBe OK   

        request = FakeRequest("POST", s"/access/vs/1/removefrom?vs_id=3&id=uw1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.removefromvs(1)(request)
        result= await(futureResult)
        status(futureResult) mustBe OK   

        request = FakeRequest("POST", s"/access/vs/1/removefrom?vs_id=3&id=gr1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.removefromvs(1)(request)
        result= await(futureResult)
        status(futureResult) mustBe OK 

        request = FakeRequest("POST", s"/access/vs/1/removefrom?vs_id=3&id=gw1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.removefromvs(1)(request)
        result= await(futureResult)
        status(futureResult) mustBe OK 
      }


      "delete a group" in {
        var permissionsController = app.injector.instanceOf[PermissionsController]
        var request = FakeRequest("POST", "/access/groups/1/edit")
        .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
        .withFormUrlEncodedBody(
          "id" -> "1",
          "action" -> "del",
          "name" -> "test",
          "description" -> "test"
        ).asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
        .withCSRFToken
        var futureResult = permissionsController.addgroup(1)(request)
        var result = await(futureResult)
        status(futureResult) mustBe OK
      }


      "list all users" in {
        var permissionsController = app.injector.instanceOf[PermissionsController]
        var request = FakeRequest("GET", s"/access/users")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        var futureResult = permissionsController.users(1)(request)
        var result: Result = await(futureResult)
        status(futureResult) mustBe OK
      }

      "manage users" in {
        var permissionsController = app.injector.instanceOf[PermissionsController]
        var request = FakeRequest("POST", s"/access/users/1/addrole?role_id=2&user_id=2")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        var futureResult = permissionsController.addrole(1)(request)
        var result= await(futureResult)
        status(futureResult) mustBe OK

        request = FakeRequest("POST", s"/access/users/1/removerole?role_id=2&user_id=2")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.removerole(1)(request)
        result = await(futureResult)
        status(futureResult) mustBe OK

        request = FakeRequest("POST", s"/access/users/1/delete?user_id=2")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.deleteuser(1)(request)
        result = await(futureResult)
        status(futureResult) mustBe OK
      }
      /*
      "add virtual sensor and write XML file" in {
        var permissionsController = app.injector.instanceOf[PermissionsController]
        val vsName = "MultiformatTemperature"
        val priority = "1"
        val className = "YourClassName"
        val description = "YourDescription"

      val fakeRequest = FakeRequest("POST", "/access/vs/addVS")
        .withFormUrlEncodedBody(
          "vsName" -> vsName,
          "priority" -> priority,
          "className" -> className,
          "description" -> description,
          "outputFieldName_0" -> "Field1",
          "outputFieldType_0" -> "double",
          "outputFieldName_1" -> "Field2",
          "outputFieldType_1" -> "double",
          "addressingKey_0" -> "Key1",
          "addressingValue_0" -> "Value1",
          "streamName_0" -> "Stream1",
          "sourceAlias_0" -> "Alias1",
          "samplingRate_0" -> "1",
          "storageSize_0" -> "1",
          "addressWrapper_0" -> "Wrapper1",
          "sourcestreamQuery_0" -> "Query1",
          "streamQuery_0" -> "StreamQuery1",
          "sourceAddressPredicatekey_0" -> "PredicateKey1",
          "sourceAddressPredicatevalue_0" -> "PredicateValue1"
        ).withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost").withCSRFToken

      val result: Future[Result] = permissionsController.addVirtualSensor()(fakeRequest)

      status(result) mustBe OK
    }*/

    }



  "GridService" should {
    /*"test functionality" in {
        var gridservice = app.injector.instanceOf[GridService]
        val to= LocalDate.now()
        val from= to.minusDays(1)
        var request = FakeRequest("GET", s"/api/sensors/MultiFormatTemperatureHandler/grid?from=${from}&to=${to}")
          .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
        var futureResult = gridservice.gridData("MultiFormatTemperatureHandler")(request)
        var result= await(futureResult)
        status(futureResult) mustBe OK

        request = FakeRequest("GET", "/api/sensors/MultiFormatTemperatureHandler/grid?format&size=&fields=")
          .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
        futureResult = gridservice.gridData("MultiFormatTemperatureHandler")(request)
        result= await(futureResult)
        status(futureResult) mustBe BAD_REQUEST

        request = FakeRequest("GET", "/api/sensors/MultiFormatTemperatureHandler/grid?format=json&size=10&fields=field1,field2&timeFormat=yyyy-MM-dd%27T%27HH:mm:ss&box=1,2,3,4")
          .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
        futureResult = gridservice.gridData("MultiFormatTemperatureHandler")(request)
        result= await(futureResult)
        status(futureResult) mustBe OK

        request = FakeRequest("GET", "/api/sensors/MultiFormatTemperatureHandler/grid/timeseries")
          .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
        futureResult = gridservice.gridTimeseries("MultiFormatTemperatureHandler")(request)
        result= await(futureResult)
        status(futureResult) mustBe OK
    }*/
  }

  "SensorService" should {
    //##################################################### sensors #####################################################
    "get api sensors in different formats" in {
      val oauthcontroller = app.injector.instanceOf[OAuth2Controller]
      var permissionsController = app.injector.instanceOf[PermissionsController]
      val sensorService = app.injector.instanceOf[SensorService]

      val requestjson = FakeRequest("GET", "/api/sensors?format=json")
      val resultjson = sensorService.sensors()(requestjson)
      status(resultjson) mustBe OK
      contentType(resultjson) mustBe Some("application/json")

      var requestcsv = FakeRequest("GET", "/api/sensors?format=csv")
      var resultcsv = sensorService.sensors()(requestcsv)
      status(resultcsv) mustBe OK
      contentType(resultcsv) mustBe Some("text/plain")

      var requestxml = FakeRequest("GET", "/api/sensors?format=xml")
      var resultxml = sensorService.sensors()(requestxml)
      status(resultxml) mustBe OK
      contentType(resultxml) mustBe Some("application/xml")

      val requestempty = FakeRequest("GET", "/api/sensors")
      val resultempty = sensorService.sensors()(requestempty)
      status(resultempty) mustBe OK
      contentType(resultempty) mustBe Some("application/json")

    }

    "get api users in different formats" in{
      val sensorService = app.injector.instanceOf[SensorService]
      var request = FakeRequest("GET", "/api/user").withHeaders(
          "Authorization" -> s"Bearer $access_token"
        )
      var result = sensorService.userInfo()(request)
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")

      request = FakeRequest("GET", "/api/user")
      result = sensorService.userInfo()(request)
      status(result) mustBe BAD_REQUEST

      request = FakeRequest("GET", "/api/user").withHeaders("Authorization" -> s"Bearer fake_token")
      result = sensorService.userInfo()(request)
      status(result) mustBe UNAUTHORIZED

      request = FakeRequest("GET", "/api/user")
          .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
      
      result = sensorService.userInfo()(request)
      status(result) mustBe FORBIDDEN

    }


    "return fields of sensor" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val sensorId = "MultiFormatTemperatureHandler"
      val fieldId = "light"
      var request = FakeRequest("GET", s"/api/sensors/$sensorId/fields/$fieldId")
          .withHeaders("Authorization" -> s"Bearer $access_token")
      var result = sensorService.sensorField(sensorId, fieldId)(request)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/plain")
    }

    "return sensor data with specified fields, size and filter" in {
        val sensorService = app.injector.instanceOf[SensorService]
        var params = Map("size" -> "10", "fields" -> "light,temperature,packet_type","filter"-> "light > 100,temperature <100")
        var request = FakeRequest("GET", s"/api/sensors/MultiFormatTemperatureHandler/data?${params.map { case (key, value) => s"$key=$value" }.mkString("&")}")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        var futureResult = sensorService.sensorData("MultiFormatTemperatureHandler")(request)
        var result: Result = await(futureResult)
        status(futureResult) mustBe OK
        contentType(futureResult) mustBe Some("application/json")
   
        val jsonResult = Json.parse(contentAsString(futureResult))
        (jsonResult \ "type").as[String] mustBe "Feature"

        val properties = (jsonResult \ "properties").as[JsObject]
        (properties \ "vs_name").as[String] mustBe "MultiFormatTemperatureHandler"

        var fields = (properties \ "fields").as[Seq[JsObject]]
        val expectedFields = Map(
              "light" -> "double",
              "temperature" -> "double",
              "packet_type" -> "double"
        )

        for ((fieldName, fieldType) <- expectedFields) {
          val field = fields.find(field => (field \ "name").as[String] == fieldName)
          field.isDefined mustBe true
          val fieldJsType = (field.get \ "type").as[String]
          fieldJsType mustBe fieldType
        }

    }

    "return sensor data in JSON format for logged in user" in {
      val sensorService = app.injector.instanceOf[SensorService]
      var params = Map("from" -> "2023-01-01","to" -> "2023-01-01","size" -> "10", "fields" -> "light,temperature,packet_type","filter"-> "light > 100,temperature <100")
      var request = FakeRequest("GET", s"/api/sensors/MultiFormatTemperatureHandler/data?${params.map { case (key, value) => s"$key=$value" }.mkString("&")}")
          .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
      var futureResult = sensorService.sensorData("MultiFormatTemperatureHandler")(request)
      var result: Result = await(futureResult)
      status(futureResult) mustBe OK
      contentType(futureResult) mustBe Some("application/json") 
    }

    "return sensor data in csv format with timeline" in {
        val sensorService = app.injector.instanceOf[SensorService]
        val paramsISO = Map("from" -> "2023-01-01","to" -> "2023-01-01","size" -> "10", "timeline" -> "timed","format"->"csv")
        val requestISO = FakeRequest("GET", s"/api/sensors/MultiFormatTemperatureHandler/data?${paramsISO.map { case (key, value) => s"$key=$value" }.mkString("&")}")
          .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
        val futureResultISO = sensorService.sensorData("MultiFormatTemperatureHandler")(requestISO)
        val resultISO: Result = await(futureResultISO)
        status(futureResultISO) mustBe OK
        contentType(futureResultISO) mustBe Some("text/plain") 
    }

    "hanlde metadata" in {
      val sensorService = app.injector.instanceOf[SensorService]
        //val wsClient: WSClient = app.injector.instanceOf[WSClient]
        //val gsnMetadata = new GsnMetadata(wsClient, "http://data.permasense.ch")

        //val resultmeta= gsnMetadata.allSensors
        //val resultmetafinished= await(resultmeta)
        //println("#####################################################")
        //println("result" + resultmeta)
        val sensorId = "MultiFormatTemperatureHandler"
        val latestValues = "true" 
        var format = "json" 
        var request = FakeRequest("GET", s"/api/sensors/$sensorId/metadata?latestValues=$latestValues&format=$format")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        var result = sensorService.sensorMetadata(sensorId)(request)
        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")

        
        format = "csv" 
        request = FakeRequest("GET", s"/api/sensors/$sensorId/metadata?latestValues=$latestValues&format=$format")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        result = sensorService.sensorMetadata(sensorId)(request)
        status(result) mustBe OK
        contentType(result) mustBe Some("text/plain")

        format = "xml" 
        request = FakeRequest("GET", s"/api/sensors/$sensorId/metadata?latestValues=$latestValues&format=$format")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        result = sensorService.sensorMetadata(sensorId)(request)
        status(result) mustBe OK
        contentType(result) mustBe Some("text/plain")
    }

       "handle sensor search" in {
        val sensorService = app.injector.instanceOf[SensorService]
        val vsnames = "MultiFormatTemperatureHandler" 
        val size = 10 
        val fields1 = "light,temperature" 
        var format = "json" 
        var request = FakeRequest("GET", s"/api/sensors/search?vsnames=$vsnames&size=$size&fields=$fields1&format=$format")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        var result = sensorService.sensorSearch(request)

        status(result) mustBe OK
        contentType(result) mustBe Some("application/json")
       
        val formatcsv = "csv" 
        val requestcsv = FakeRequest("GET", s"/api/sensors/search?vsnames=$vsnames&size=$size&fields=$fields1&format=$formatcsv")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        val resultcsv = sensorService.sensorSearch(requestcsv)

        status(resultcsv) mustBe OK
        contentType(resultcsv) mustBe Some("application/zip")

        val formatxml = "xml" 
        val requestxml = FakeRequest("GET", s"/api/sensors/search?vsnames=$vsnames&size=$size&fields=$fields1&format=$formatxml")
          .withHeaders("Authorization" -> s"Bearer $access_token")
        val resultxml = sensorService.sensorSearch(requestxml)

        status(resultxml) mustBe OK
        contentType(resultxml) mustBe Some("text/plain") 

       }
       
    

        //download     
        "download" in {
          val sensorService = app.injector.instanceOf[SensorService]
          var request = FakeRequest(POST, "/api/sensors/download")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
          var futureResult = sensorService.download()(request)
          var result: Result = await(futureResult)
          status(futureResult) mustBe OK
        }
      
        "return FORBIDDEN FOR not authorized resource" in {
          val sensorService = app.injector.instanceOf[SensorService]
          var params = Map("size" -> "10", "fields" -> "light,temperature,packet_type","filter"-> "light > 100,temperature <100")
          var request = FakeRequest("GET", s"/api/sensors/MultiFormatTemperatureHandler1/data?${params.map { case (key, value) => s"$key=$value" }.mkString("&")}")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
          var futureResult = sensorService.sensorData("MultiFormatTemperatureHandler1")(request)
          var result = await(futureResult)
         status(futureResult) mustBe FORBIDDEN 
        }


        "return FORBIDDEN FOR not authorized resource (accessToken)" in {
          val sensorService = app.injector.instanceOf[SensorService]
          var params = Map("size" -> "10", "fields" -> "light,temperature,packet_type","filter"-> "light > 100,temperature <100")
          var request = FakeRequest("GET", s"/api/sensors/MultiFormatTemperatureHandler1/data?${params.map { case (key, value) => s"$key=$value" }.mkString("&")}")
            .withHeaders("Authorization" -> s"Bearer $access_token")
          var futureResult = sensorService.sensorData("MultiFormatTemperatureHandler1")(request)
          var result= await(futureResult)
          status(futureResult) mustBe FORBIDDEN 
        }
/*
      "upload sensor data and forward it to GSN core return internal server error" in {
        val sensorService = app.injector.instanceOf[SensorService]
        var permissionsController = app.injector.instanceOf[PermissionsController]
        var request = FakeRequest("POST", s"/access/vs/1/addto?vs_id=3&id=uw1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        var futureResult = permissionsController.addtovs(1)(request)
        var result= await(futureResult)
        status(futureResult) mustBe OK  

        val sensorId = "MultiFormatTemperatureHandlerzeromq"
        val jsonData = Json.obj(
          "properties" -> Json.obj(
            "vs_name" -> "MultiFormatTemperatureHandlerzeromq",
            "fields" -> Json.arr(
              Json.obj(
                "name" -> "timestamp",
                "type" -> "time",
                "unit" -> "ms"
              ),
              Json.obj(
                "name" -> "light",
                "type" -> "DOUBLE"
              ),
              Json.obj(
                "name" -> "temperature",
                "type" -> "DOUBLE"
              ),
              Json.obj(
                "name" -> "packet_type",
                "type" -> "DOUBLE"
              )
            ),
            "values" -> Json.arr(
              Json.arr("1699437381297", "42.0", "79.75","2")
            )
          )
        )

        val request1 = FakeRequest("POST", s"/api/sensors/$sensorId/data")
          .withHeaders("Authorization" -> s"Bearer $access_token")
          .withJsonBody(jsonData)
        val futureResult1= sensorService.uploadSensorData(sensorId)(request1)
        val result1 = await(futureResult1) 
        val stringresult= contentAsString(futureResult1)
        contentAsString(futureResult1) must include("error")
        contentAsString(futureResult1) must include("Packet forwarding to GSN core failed.")
        

        //remove write rights: 
        request = FakeRequest("POST", s"/access/vs/1/removefrom?vs_id=3&id=uw1")
            .withSession("pa.p.id" -> "password", "pa.u.id" -> "root@localhost")
            .withCSRFToken
        futureResult = permissionsController.removefromvs(1)(request)
        result= await(futureResult)
        status(futureResult) mustBe OK  
      }
*/

    "check for authorization" in {
      val sensorService = app.injector.instanceOf[SensorService]
      val auth= app.injector.instanceOf[com.feth.play.module.pa.PlayAuthenticate]
      val handler = new GSNScalaDeadboltHandler(auth)
      var params = Map("from" -> "2023-01-01","to" -> "2023-01-01","size" -> "10", "fields" -> "light,temperature,packet_type","filter"-> "light > 100,temperature <100")
      var request = FakeRequest("GET", s"/api/sensors/MultiFormatTemperatureHandler/data?${params.map { case (key, value) => s"$key=$value" }.mkString("&")}")
      val result =  handler.beforeAuthCheck(request)
      val finalres= await(result)
      println(result)
    }
      

  }



    



}
