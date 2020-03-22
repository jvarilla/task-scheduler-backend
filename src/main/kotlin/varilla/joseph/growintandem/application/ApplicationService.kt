package varilla.joseph.growintandem.application

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.koin.core.KoinComponent
import varilla.joseph.growintandem.utils.models.Plant
import java.time.Instant

interface ApplicationService :KoinComponent{

  /**
   * Get a list of plants
   * @return  A JsonArray of plants
   */
  suspend fun getPlantsList() :JsonArray

  /**
   * Get a plant by id
   * @return A JsonObject representation of a plantn
   */
  suspend fun getPlantById(id :String) : JsonObject

  /**
   * Gets the watering schedule of a specific plant provided
   * the id, the number of weeks, whether to allow weekends,
   * and the start date
   *
   * @param id  The id of the plant to get the schedule for
   * @param startDate The first day of the schedule to produce
   * @param numWeeks  The number of weeks a schedule should be made for
   * @param allowWeekends Whether to allow the schedule to schedule for weekends or not
   *
   * @return  A JsonObject containing the plant id, the plant name, and an array of watering date times
   */
  suspend fun getPlantWateringSchedule(id :String, startDate :Instant,
                                       numWeeks :Int, allowWeekends :Boolean) : JsonObject

  /**
   * Gets the watering schedule of all plants provided
   * the number of weeks, whether to allow weekends,
   * and the start date
   *
   * @param startDate The first day of the schedule to produce
   * @param numWeeks  The number of weeks a schedule should be made for
   * @param allowWeekends Whether to allow the schedule to schedule for weekends or not
   *
   * @return  A JsonObject containing the schedule for all plants
   */
  suspend fun getAllPlantWateringSchedules(startDate : Instant,
                                           numWeeks :Int, allowWeekends :Boolean) :JsonObject


  /**
   * Creates a new plant
   *
   * @param plantName Name of plant to be created
   * @param waterNumDays  Number of days a plant can go without watering
   */
  suspend fun createPlant(plantName :String, waterNumDays :Int) : JsonObject


  /**
   * Remove a plant
   * @param id  The id of the plant to remove
   *
   * @return  The Plant that was removed
   */
  suspend fun removePlant(id :String) : JsonObject
}
