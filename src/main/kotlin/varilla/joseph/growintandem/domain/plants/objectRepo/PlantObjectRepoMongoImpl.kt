package varilla.joseph.growintandem.domain.plants.objectRepo

import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.ext.mongo.findAwait
import io.vertx.kotlin.ext.mongo.findOneAwait
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.dsl.module
import varilla.joseph.growintandem.utils.domain.PlantNotFoundException
import varilla.joseph.growintandem.utils.models.Plant
import varilla.joseph.growintandem.utils.models.toPlant

class PlantObjectRepoMongoImpl :PlantObjectRepo, KoinComponent {
  private val mongoClient :MongoClient by inject()

  /**
   * Get plants list
   * @return  List of plants
   */
  override suspend fun getPlantsList() :List<Plant> {
    try {
      val response =  mongoClient.findOneAwait("plants", jsonObjectOf(), jsonObjectOf()) ?: throw PlantNotFoundException()
      return listOf<Plant>(response.toPlant())
    } catch (throwable :Throwable) {
        throw throwable
    }

       //listOf<Plant>(Plant("1", "1", 1))
//    return mongoClient.findAwait("plants", jsonObjectOf()).map{
//      it.toPlant()
//    }
  }

  /**
   * Get plant by id
   * @return  A plant
   */
  override suspend fun getPlantById(id :String) : Plant {
    try {
      println("Before query")
      val response =  mongoClient.findOneAwait("plants", jsonObjectOf("name" to "Fiddle Leaf Fig"), jsonObjectOf()) ?: throw PlantNotFoundException()
      println("after query")
      response.put("id", response.getValue("id"))

      response.remove("_id")
      println("RESPONSE $response")
      return Plant(id = "1", name = response.getString("name") ,waterEveryNumDays = response.getInteger("water_after")) // response.toPlant()
    } catch (throwable :Throwable) {
      println("shit happened")
      println(throwable.stackTrace)
      println(throwable.localizedMessage)
      throw throwable
    }
  }

}
