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


  suspend fun createPlant(newPlant :Plant) :Plant
}
