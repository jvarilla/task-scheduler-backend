package varilla.joseph.growintandem.domain.plants.domainService

import io.vertx.core.json.JsonObject
import org.koin.core.KoinComponent
import varilla.joseph.growintandem.utils.models.Plant
import varilla.joseph.growintandem.utils.models.PlantWateringSchedule
import java.time.Instant

interface PlantDomainService :KoinComponent {

  /**
   * Get list of plants
   * @return List of plants as DTO's
   */
  suspend fun getPlantsList() :List<Plant>

  /**
   * Get a plant by id
   * @return A plant as DTO
   */
  suspend fun getPlantById(id :String) :Plant

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
   * @return  A PlantWatering Schedule object
   */
  suspend fun getPlantWateringSchedule(id :String, startDate : Instant,
                                       numWeeks :Int, allowWeekends :Boolean) : PlantWateringSchedule

  /**
   * Create a new plant
   *
   * @param newPlant  New plant to be created
   *
   * @return  The a created plant that is confirmed to be correct and added
   */
  suspend fun createPlant(newPlant :Plant) :Plant

  /**
   * Remove a plant
   * @param id  The id of the plant to remove
   *
   * @return  The Plant that was removed
   */
  suspend fun removePlant(id :String) :Plant
}
