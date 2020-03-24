package varilla.joseph.growintandem.application

import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.koin.core.KoinComponent
import org.koin.core.inject
import varilla.joseph.growintandem.domain.plants.domainService.PlantDomainService
import varilla.joseph.growintandem.utils.domain.PlantNotFoundException
import varilla.joseph.growintandem.utils.http.RequestErrorException
import varilla.joseph.growintandem.utils.models.*
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*

class ApplicationServiceImpl :ApplicationService, KoinComponent {
  private val plantDomainService by inject<PlantDomainService>()

  override suspend fun getPlantsList(): JsonArray {
    try {

      // Get the plants list from the domain service
      val plants = plantDomainService.getPlantsList()

      return JsonArray(plants)

    } catch(throwable :Throwable) {
        when(throwable) {
          else -> throw throwable
        }
    }
  }


  override suspend fun getPlantById(id: String): JsonObject {
    try {

      // Get a plant by id from the domain service
      val plant = plantDomainService.getPlantById(id)

      return plant.toJsonObject()

    } catch (throwable: Throwable) {
        when(throwable) {
          is PlantNotFoundException -> throw RequestErrorException(404, "Plant Not Found")
          else -> throw throwable
        }
    }
  }


  override suspend fun getPlantWateringSchedule(id :String, startDate : Instant,
                                                numWeeks :Int, allowWeekends :Boolean) : JsonObject {
    try {
      // Get the plants watering schedule
      return plantDomainService.getPlantWateringSchedule(id, startDate, numWeeks, allowWeekends).toJsonObject()

    } catch (throwable: Throwable) {
      when(throwable) {
        is PlantNotFoundException -> throw RequestErrorException(404, "Plant Not Found")
        else -> throw throwable
      }
    }
  }

  override suspend fun getAllPlantWateringSchedules(startDate : Instant,
                                                    numWeeks :Int, allowWeekends :Boolean) :JsonObject {
    // Get the full list of plants
    val listOfAllPlants = plantDomainService.getPlantsList()

    // Get the schedule for each plant and put into an array
    var plantsSchedules = mutableListOf<PlantWateringDate>()
    listOfAllPlants.forEach{ plant ->
      plantDomainService
        .getPlantWateringSchedule(plant.id, startDate, numWeeks, allowWeekends)
        .wateringSchedule.forEach{date ->
          plantsSchedules.add(PlantWateringDate(plant.id, plant.name, date))
      }}

    // sort the plantsSchedules
    plantsSchedules = plantsSchedules.sortedBy{ plantWateringSchedule -> plantWateringSchedule.date  }.toMutableList()
    val scheduleStartDate = plantsSchedules[0].date
    val scheduleEndDate = plantsSchedules[plantsSchedules.size - 1 ].date


    var weekStartDate = scheduleStartDate
    var weekEndDate = weekStartDate

    var weekCounter = 1
    // Create the schedule calendar object by composition
    var currentDate = scheduleStartDate
    var currentDateInfo = currentDate.atZone(ZoneId.systemDefault())
    val oneDayAfterScheduleEndDateInstant = (scheduleEndDate.plus(Duration.ofDays(1L)))
    val oneDayAfterScheduleEndDate = Date.from(oneDayAfterScheduleEndDateInstant)

    val oneDayAfterScheduleEndDateInfo = oneDayAfterScheduleEndDateInstant.atZone(ZoneId.systemDefault())


    var plantSchedulesIdx = 0
    var plantWateringScheduleWeeksList = mutableListOf<ScheduleCalendarWeek>()
    var plantWaterScheduleWeekDayList = mutableListOf<ScheduleCalendarDay>()
    var plantWateringScheduleDayPlantsList = mutableListOf<PlantWateringDate>()
    while (
        Date.from(currentDate).before(oneDayAfterScheduleEndDate) && plantSchedulesIdx < plantsSchedules.size) {
        var currentPlantWateringDate = plantsSchedules[plantSchedulesIdx]
        var currentPlantWateringDateInfo = (currentPlantWateringDate.date).atZone(ZoneId.systemDefault())


        // If the item is on the same date
        if (
          currentDateInfo.dayOfMonth == currentPlantWateringDateInfo.dayOfMonth
          && currentDateInfo.month == currentPlantWateringDateInfo.month
          && currentDateInfo.year == currentPlantWateringDateInfo.year) {
          // Add the plant to the daily watering list
          plantWateringScheduleDayPlantsList.add(currentPlantWateringDate)
          // Advance a day in the plant schedule list
          plantSchedulesIdx++
        } else { // Otherwise this next plant watering date lands on another date
            // If so then first add the day to the week
          var currentWateringScheduleDay =
            ScheduleCalendarDay(
              date = currentDate,
              dayOfWeek = currentDate.atZone(ZoneId.systemDefault()).dayOfWeek.value,
              plants = plantWateringScheduleDayPlantsList.toList())
          println("DAY: ${Json.encode(currentWateringScheduleDay)}")
          // Add the currentWater schedule day to the week
          plantWaterScheduleWeekDayList.add(currentWateringScheduleDay)


          // Check to see if this is a Saturday or last day of the schedule
          if (
            currentDateInfo.dayOfWeek == DayOfWeek.SATURDAY
            || Date.from(currentDate) == Date.from(scheduleEndDate)) {

            // If so then set the week end date to the current date (Saturday)
            weekEndDate = currentDate

            // Create the ScheduleWeekObject
            var currentScheduleWeek = ScheduleCalendarWeek(
              startDate = weekStartDate,
              endDate = weekEndDate,
              weekNum = weekCounter,
              days = plantWaterScheduleWeekDayList.toList()
            )

            // Add to the weeks list
            plantWateringScheduleWeeksList.add(currentScheduleWeek)

            // Reset the week day list
            plantWaterScheduleWeekDayList = mutableListOf()


            // Increment the week counter
            weekCounter++

            // Make the week start date equal to the next day (sunday)

            weekStartDate = currentDate.plus(Duration.ofDays(1L))

          }

          // Then reset the list of plants to water on the current day
          plantWateringScheduleDayPlantsList = mutableListOf<PlantWateringDate>()




          // Advance the currentDate by one day
          currentDate = currentDate.plus(Duration.ofDays(1L))
          currentDateInfo = currentDate.atZone(ZoneId.systemDefault())
        }
    }

    // Construct the schedule calendar object
    return ScheduleCalendar(
      startDate = scheduleStartDate,
      endDate = scheduleEndDate,
      numWeeks = numWeeks,
      allowWeekends = allowWeekends,
      weeks = plantWateringScheduleWeeksList.toList()
    ).toJsonObject()
  }

  override suspend fun createPlant(plantName: String, waterNumDays: Int): JsonObject {
    try {
      val id = UUID.randomUUID().toString()

      // Create the plant and pass it to the Plant Domain Service
      return plantDomainService.createPlant(
        Plant(id = id, name = plantName, waterEveryNumDays = waterNumDays)).toJsonObject()

    } catch (throwable :Throwable) {
      throw  throwable
    }
  }

  override suspend fun removePlantById(id: String): JsonObject {
    try {
      // Remove the plant and convert to JsonObject
      return plantDomainService.removePlantById(id = id).toJsonObject()
    } catch (throwable :Throwable) {
      when(throwable) {
        else -> throw throwable
      }
    }
  }


}
