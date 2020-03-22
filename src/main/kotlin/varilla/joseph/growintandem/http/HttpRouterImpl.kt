package varilla.joseph.growintandem.http

import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.launch
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import varilla.joseph.growintandem.application.ApplicationService
import varilla.joseph.growintandem.utils.http.*
import varilla.joseph.growintandem.utils.models.Plant
import java.lang.Exception
import java.time.Instant
import kotlin.coroutines.CoroutineContext

class HttpRouterImpl(private val vertx : Vertx,
                     private val coroutineContext :CoroutineContext) : HttpRouter, KoinComponent {

  private val applicationService by inject<ApplicationService>()
  private val coroutineScope = CoroutineScope(coroutineContext)

  override suspend fun getRouter(): Router {
    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create());
    router.route().handler(
      CorsHandler.create("*")
      .allowedMethod(io.vertx.core.http.HttpMethod.GET)
      .allowedMethod(io.vertx.core.http.HttpMethod.POST)
      .allowedMethod(io.vertx.core.http.HttpMethod.DELETE)
      .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
      .allowedHeader("Access-Control-Request-Method")
      .allowedHeader("Access-Control-Allow-Credentials")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Headers")
      .allowedHeader("Content-Type"));
    // Add body parsing capability to router
    router.route().handler(BodyHandler.create())

    // API v1 base
    val apiBase1 = "/api/v1"

    // Default Route
    router.get("$apiBase1")
      .handler{ routingContext: RoutingContext ->
        val response = routingContext.response()
        response.putHeader("content-type", "text/plain")
        response.end("Hello from Vert.x")
      }


    // Add Routes
    router.get("$apiBase1/plants").coroutineHandler(this::getPlantsListHandler)
    router.get("$apiBase1/plants/:id").coroutineHandler(this::getPlantByIdHandler)

    // Example:
    // host:port/api/v1/plants/123/watering-schedule?weeks=12&start-date=2003-11-20T11:11:11Z&allow-weekends=false
    router.get("$apiBase1/plants/:id/watering-schedule").coroutineHandler(this::getPlantWateringSchedule)
    router.post("$apiBase1/plants").coroutineHandler(this::createPlantHandler)

    router.delete("$apiBase1/plants/:id").coroutineHandler(this::removePlantHandler)
    return router
  }

  override suspend fun getPlantsListHandler(event :RoutingContext) {

    var request = event.request()
    var response = event.response()

    try {
      // Get the plants list from the application service
      val plants = applicationService.getPlantsList()

      // Serialize it
      val msg = Json.encodePrettily(plants)

      // Send the message
      response.sendAsJSONWithStatusCode(msg, 200)
    } catch (reqErrorException :RequestErrorException) {

      // Send the request error message
      response.sendAsJSONWithStatusCode(
          Json.encodePrettily(reqErrorException.toErrorMessageObj()), reqErrorException.statusCode)
    } catch(throwable :Throwable) {
        when(throwable) {
          else -> { // If unknown send 500 error
            response.sendAsJSONWithStatusCode(
              Json.encodePrettily(SERVER_ERROR_MESSAGE_OBJECT),
              SERVER_ERROR_MESSAGE_OBJECT.statusCode) }
        }
    }
  }

  override suspend fun getPlantByIdHandler(event :RoutingContext) {

    val request = event.request()
    val response = event.response()

    try {

      // Get the plants by id from the application service
      val id = request.getParam("id") ?: "id"
      val plant = applicationService.getPlantById(id)
      val msg = Json.encodePrettily(plant)
      event.response().sendAsJSONWithStatusCode(msg, 200)

    } catch (reqErrorException :RequestErrorException) {

      // Send any identified request error
      response.sendAsJSONWithStatusCode(
        Json.encodePrettily(reqErrorException.toErrorMessageObj()), reqErrorException.statusCode)

    } catch(throwable :Throwable) {

      when(throwable) {
        else -> { // Otherwise send 500 error
          response.sendAsJSONWithStatusCode(
            Json.encodePrettily(SERVER_ERROR_MESSAGE_OBJECT),
            SERVER_ERROR_MESSAGE_OBJECT.statusCode) }
      }
    }
  }


  override suspend fun getPlantWateringSchedule(event :RoutingContext) {

    var request = event.request()
    var response = event.response()

    try {
      // Get the query string parameters
      val plantId = request.getParam("id").trim().toLowerCase()

      // Get the number of weeks to get the schedule for default to 1 week if not specified
      val numWeeksToGetScheduleFor = Integer.parseInt(
        ((request.getParam("weeks") ?: "1").trim()))

      // Parse the start date but default to today's date
      val scheduleStartDateString = (request.getParam("start-date") ?: "").trim()

      // Get the instant
      var scheduleStartDate :Instant
      // If the length is less than 1 it means it was null so set the default to current time
      scheduleStartDate =
        if (scheduleStartDateString.isEmpty()) {
          Instant.now()
      } else {
          Instant.parse(scheduleStartDateString)
      }

      // Make allow-weekends false by default if not true
      val scheduleForWeekends =
        (request.getParam("allow-weekends") ?: "false" ).trim().toLowerCase() == "true"


      // Get the plants list from the application service
      //val plants = applicationService.getPlantsList()
      val receivedParams = jsonObjectOf(
        "id" to plantId,
        "weeks" to numWeeksToGetScheduleFor,
        "start-date" to scheduleStartDate,
        "allow-weekends" to scheduleForWeekends
      )

      val wateringSchedule :Any

      wateringSchedule = if (plantId == "all") {
        applicationService.getAllPlantWateringSchedules(
          startDate = scheduleStartDate,
          numWeeks = numWeeksToGetScheduleFor,
          allowWeekends = scheduleForWeekends
          )
      } else {
        applicationService.getPlantWateringSchedule(
          id = plantId,
          startDate = scheduleStartDate,
          numWeeks = numWeeksToGetScheduleFor,
          allowWeekends = scheduleForWeekends
          )
      }


      // Serialize it
      val msg = Json.encodePrettily(wateringSchedule)

      // Send the message
      response.sendAsJSONWithStatusCode(msg, 200)
    } catch (reqErrorException :RequestErrorException) {
      // Send the request error message
      response.sendAsJSONWithStatusCode(
        Json.encodePrettily(reqErrorException.toErrorMessageObj()), reqErrorException.statusCode)
    } catch(throwable :Throwable) {
      println(throwable)
        when(throwable) {
          else -> { // If unknown send 500 error
            response.sendAsJSONWithStatusCode(
              Json.encodePrettily(SERVER_ERROR_MESSAGE_OBJECT),
              SERVER_ERROR_MESSAGE_OBJECT.statusCode) }
        }
    }
  }

  override suspend fun createPlantHandler(event: RoutingContext) {
    try {
      // Get the response body
      val requestPayload  = event.body.toJsonObject()

      // Parse Params
      val plantName :String = requestPayload.getString("plantName") ?: throw Exception()
      val waterNumDays :Int = requestPayload.getInteger("waterNumDays") ?: throw Exception()


      val response = applicationService.createPlant(plantName = plantName, waterNumDays =  waterNumDays)

      // Send back response
      event.response().sendAsJSONWithStatusCode(response.toString(), 201)
    } catch(throwable :Throwable) {
      println(throwable)
      when(throwable) {
        else -> { // If unknown send 500 error
          event.response().sendAsJSONWithStatusCode(
            Json.encodePrettily(SERVER_ERROR_MESSAGE_OBJECT),
            SERVER_ERROR_MESSAGE_OBJECT.statusCode) }
      }
    }

  }

  override suspend fun removePlantHandler(event: RoutingContext) {
    try {
      // Get the id
      val targetId = event.request().getParam("id") ?: throw Exception()

      // Make the call to app service to remove the plant
      val deletedPlantObj = applicationService.removePlant(id = targetId)

      // Send back response
      event.response().sendAsJSONWithStatusCode(deletedPlantObj.toString(), 202)

    } catch (throwable :Throwable) {
      println(throwable)
      when(throwable) {
        else -> { // If unknown send 500 error
          event.response().sendAsJSONWithStatusCode(
            Json.encodePrettily(SERVER_ERROR_MESSAGE_OBJECT),
            SERVER_ERROR_MESSAGE_OBJECT.statusCode) }
      }
    }
  }
  // This is needed to make coroutines work. Don't worry about how it works for now.
  private fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit) {
      handler { ctx ->
        coroutineScope
          .launch(ctx.vertx().dispatcher()) {
          try {
            fn(ctx)
          } catch (e: Throwable) {
            ctx.fail(e)
          }
        }
      }
    }



}
