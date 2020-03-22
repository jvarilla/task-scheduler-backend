package varilla.joseph.growintandem.domain.plants.objectRepo

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.koin.core.KoinComponent
import varilla.joseph.growintandem.utils.domain.PlantNotFoundException
import varilla.joseph.growintandem.utils.models.Plant

class PlantObjectRepoInMemoryImpl :PlantObjectRepo , KoinComponent {

  private val plantDataStore = mutableMapOf<String, Plant>()

  private val plantMap = mutableMapOf<String, Plant>()


  override suspend fun getPlantsList(): List<Plant> {
      try {
        // Return the list of all plants
        return plantMap.values.toList()

      } catch(throwable :Throwable) {
        when (throwable) {
          else -> throw throwable
        }
      }
  }

  override suspend fun getPlantById(id :String): Plant {
      try {
          // Get the plant by id from data store otherwise thrown 404 not found exception
          return plantMap[id] ?: throw PlantNotFoundException()

      } catch (throwable :Throwable) {
        when (throwable) {
          else -> throw throwable
        }
      }

  }

  override suspend fun createPlant(newPlant: Plant): Plant {
    try {
      plantDataStore[newPlant.id] = newPlant
      return newPlant
    } catch (throwable :Throwable) {
      when (throwable) {
        else -> throw throwable
      }
    }

  }

  override suspend fun removePlant(id: String): Plant {
    try {
      // Get the plant to remove
      var plantToReturn :Plant = plantDataStore.get(id) ?: throw PlantNotFoundException()

      // Remove plant by id
      plantDataStore.remove(id)

      return plantToReturn
    } catch (throwable :Throwable) {
      when (throwable) {
        else -> throw throwable
      }
    }
  }
}


