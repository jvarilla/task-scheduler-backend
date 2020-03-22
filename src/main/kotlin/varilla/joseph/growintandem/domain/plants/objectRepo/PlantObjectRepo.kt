package varilla.joseph.growintandem.domain.plants.objectRepo

import org.koin.core.KoinComponent
import varilla.joseph.growintandem.utils.models.Plant

interface PlantObjectRepo :KoinComponent {

  /**
   * Get plants list
   * @return  List of plants
   */
  suspend fun getPlantsList() :List<Plant>

  /**
   * Get plant by id
   * @return  A plant
   */
  suspend fun getPlantById(id :String) :Plant

  /**
   * Create a plant
   * @param newPlant  The plant to add to the data source
   *
   * @return  The Plant if added successfully
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
