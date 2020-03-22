package varilla.joseph.growintandem.domain.plants.objectRepo

import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.ext.mongo.findAwait
import io.vertx.kotlin.ext.mongo.findOneAndDeleteAwait
import io.vertx.kotlin.ext.mongo.findOneAwait
import io.vertx.kotlin.ext.mongo.insertAwait
import org.koin.core.KoinComponent
import org.koin.core.inject
import varilla.joseph.growintandem.utils.domain.PlantNotFoundException
import varilla.joseph.growintandem.utils.models.Plant
import varilla.joseph.growintandem.utils.models.toPlant

class PlantObjectRepoMongoImpl :PlantObjectRepo, KoinComponent {
  private val mongoClient: MongoClient by inject()

  /**
   * Get plants list
   * @return  List of plants
   */
  override suspend fun getPlantsList(): List<Plant> {
    try {
      return mongoClient.findAwait("plants", jsonObjectOf()).map {
        it.toPlant()
      }
    } catch (throwable: Throwable) {
      throw throwable
    }
  }


  /**
   * Get plant by id
   * @return  A plant
   */
  override suspend fun getPlantById(id: String): Plant {
    try {
      val response = mongoClient.findOneAwait("plants", jsonObjectOf("_id" to id), jsonObjectOf())
        ?: throw PlantNotFoundException()

      return Plant(
        id = response.getString("_id"),
        name = response.getString("name"),
        waterEveryNumDays = response.getInteger("water_after")
      )
    } catch (throwable: Throwable) {
      println("shit happened")
      println(throwable.stackTrace)
      println(throwable.localizedMessage)
      throw throwable
    }
  }

  override suspend fun createPlant(newPlant: Plant): Plant {
    try {
      // Do id rotation
      var plantDocument = newPlant.toJsonObject()
      plantDocument.put("_id", newPlant.id)
      plantDocument.remove("id")

      mongoClient.insertAwait(collection = "plants", document = plantDocument)
      // Return the plant if successful
      return newPlant
    } catch (throwable: Throwable) {
      when (throwable) {
        else -> throw throwable
      }
    }
  }

  override suspend fun removePlant(id: String): Plant {
    try {
        // Delete plant and return it or throw a Plant Not Found Exceptions
        return (mongoClient.findOneAndDeleteAwait("plants", jsonObjectOf("_id" to id))
            ?: throw PlantNotFoundException()).toPlant()
    } catch (throwable :Throwable) {
      when (throwable) {
        else -> throw throwable
      }
    }
  }

}

