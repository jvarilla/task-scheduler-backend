package varilla.joseph.growintandem.utils.models

import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonObjectOf
import org.omg.CORBA.DynAnyPackage.Invalid
import java.time.Instant

data class Plant (var id:String, var name:String, var waterEveryNumDays :Int ) {
  fun toJsonObject() : JsonObject {
    return jsonObjectOf(
      "id" to this.id,
      "name" to this.name,
      "water_after" to this.waterEveryNumDays
    )
  }
}

fun JsonObject.toPlant() :Plant {
  try {
    return Plant(
      id = this.getString("id"),
      name = this.getString("name"),
      waterEveryNumDays = this.getInteger("water_after")
    )
  } catch (exception :Exception) {
      throw InvalidPlantException()
  }

}

class InvalidPlantException :Exception()


data class PlantWateringSchedule(var plantId:String, var plant :Plant, var wateringSchedule :List<Instant>) {
  fun toJsonObject() :JsonObject {
    return jsonObjectOf(
      "plant_id" to this.plantId,
      "plant" to this.plant.toJsonObject(),
      "watering_schedule" to this.wateringSchedule
    )
  }
}
