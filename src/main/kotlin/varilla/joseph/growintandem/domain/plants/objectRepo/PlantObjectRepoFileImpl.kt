package varilla.joseph.growintandem.domain.plants.objectRepo

import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.koin.core.KoinComponent
import varilla.joseph.growintandem.utils.domain.PlantNotFoundException
import varilla.joseph.growintandem.utils.models.Plant

class PlantObjectRepoFileImpl :PlantObjectRepo , KoinComponent {
  private val sampleFile = """
     [
  {
    "name": "Fiddle Leaf Fig",
    "water_after": "7 days"
  },
  {
    "name": "Snake Plant",
    "water_after": "14 days"
  },
  {
    "name": "Money Tree",
    "water_after": "14 days"
  },
  {
    "name": "Bird's Nest Fern",
    "water_after": "3 days"
  },
  {
    "name": "Croton",
    "water_after": "7 days"
  },
  {
    "name": "Bell Pepper Plant",
    "water_after": "3 days"
  },
  {
    "name": "Strawberry Plant",
    "water_after": "3 days"
  },
  {
    "name": "Dracaena",
    "water_after": "14 days"
  },
  {
    "name": "Spider Plant",
    "water_after": "7 days"
  },
  {
    "name": "Jade",
    "water_after": "14 days"
  },
  {
    "name": "Wavy Fern",
    "water_after": "2 days"
  }
  ]
"""

  private val jsonArrOfSampleData = JsonArray(sampleFile)

  private var loaded = false

  private val plantMap = mutableMapOf<String, Plant>()

  suspend fun loadMap() {
    var id = 0
    jsonArrOfSampleData.forEach {
      var currentObj = JsonObject(it.toString())
      println(currentObj)
      var numDays = currentObj.getString("water_after").trim().split(" ")[0]
      println(numDays)
      plantMap[id.toString()] = Plant(id.toString(), currentObj.getString("name"), Integer.parseInt(numDays))
      id++
    }
    loaded = true
  }


  override suspend fun getPlantsList(): List<Plant> {
      if (!loaded) {
      loadMap()
    }
      try {
        // Return the list of all plants
        return plantMap.values.toList()

      } catch(throwable :Throwable) {

        throw throwable

      }
  }

  override suspend fun getPlantById(id :String): Plant {
    if (!loaded) {
      loadMap()
    }
      try {
          // Get the plant by id from data store otherwise thrown 404 not found exception
          return plantMap[id] ?: throw PlantNotFoundException()

      } catch (throwable :Throwable) {
        throw throwable
      }

  }
}
