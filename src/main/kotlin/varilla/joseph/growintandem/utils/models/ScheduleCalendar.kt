package varilla.joseph.growintandem.utils.models

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import java.time.Instant

data class ScheduleCalendar(
   val startDate :Instant,
   val endDate:Instant,
   val numWeeks :Int,
   val allowWeekends :Boolean,
   val weeks :List<ScheduleCalendarWeek>
  )

data class ScheduleCalendarWeek(
  val startDate: Instant,
  val endDate: Instant,
  val weekNum :Int,
  val days :List<ScheduleCalendarDay>
)

data class ScheduleCalendarDay(
  val date :Instant,
  val dayOfWeek :Int, // Java is 1-7 and JS is 0-6
  val plants :List<PlantWateringDate>
)

fun ScheduleCalendar.toJsonObject() : JsonObject {
  return JsonObject(Json.encodePrettily(this))
}

data class PlantWateringDate(val plantId :String, val plantName :String, val date:Instant)
