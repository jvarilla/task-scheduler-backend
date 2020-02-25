package varilla.joseph.growintandem.http

import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import org.koin.core.KoinComponent

interface HttpRouter :KoinComponent {
  /**
   * Returns the router
   */
  suspend fun getRouter(): Router

  /**
   * Returns all the plants in the system
   */
  suspend fun getPlantsListHandler(event :RoutingContext): Unit;

  /**
   * Returns a plant with a specific id
   */
  suspend fun getPlantByIdHandler(event :RoutingContext): Unit;

  /**
   * Gets the watering schedule of a specific plant provided
   * the id, the number of weeks, whether to allow weekends,
   * and the start date
   */
  suspend fun getPlantWateringSchedule(event :RoutingContext) : Unit


}
