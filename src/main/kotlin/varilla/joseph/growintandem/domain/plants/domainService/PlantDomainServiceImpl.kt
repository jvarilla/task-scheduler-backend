package varilla.joseph.growintandem.domain.plants.domainService

import org.koin.core.KoinComponent
import org.koin.core.inject
import varilla.joseph.growintandem.domain.plants.objectRepo.PlantObjectRepo
import varilla.joseph.growintandem.utils.domain.NUMBER_OF_DAYS_IN_WEEK
import varilla.joseph.growintandem.utils.models.Plant
import varilla.joseph.growintandem.utils.models.PlantWateringSchedule
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.DayOfWeek

class PlantDomainServiceImpl :PlantDomainService, KoinComponent {

  private val plantObjectRepo :PlantObjectRepo by inject()

  override suspend fun getPlantsList(): List<Plant> {
    try {
      // Get the plant list from the obj repo
      return plantObjectRepo.getPlantsList()

    } catch (throwable :Throwable) {
      throw throwable
    }
  }

  override suspend fun getPlantById(id: String): Plant {
    try {

      // Get a plant by id from the object repo
      return plantObjectRepo.getPlantById(id)

    } catch (throwable :Throwable) {
      throw throwable
    }
  }

  override suspend fun getPlantWateringSchedule(id: String, startDate: Instant, numWeeks: Int,
      allowWeekends: Boolean) :PlantWateringSchedule {
    try {
      // Get the plant
      val thePlant = plantObjectRepo.getPlantById(id)

      // Return the watering schedule
      return PlantWateringSchedule(
        plantId = id,
        plant = thePlant,
        wateringSchedule =
          createWateringSchedule(
            startDate = startDate,
            numWeeks = numWeeks,
            allowWeekends = allowWeekends,
            wateringDayFrequency = thePlant.waterEveryNumDays.toLong()
        ))
    } catch (throwable: Throwable) {
      throwable.printStackTrace()
      throw throwable
    }
  }








  /**
   * Returns a list of watering dates for a plant
   * @param startDate The requested start date of the schedule
   * @param numWeeks  The number of weeks to make the schedule for
   * @param allowWeekends Whether to allow schedules to have watering dates on weekends or not
   * @param wateringDayFrequency  Watering frequency for a plant in days as a long
   *
   * @return  A list of all watering dates for the schedule
   */
  private suspend fun createWateringSchedule(startDate :Instant, numWeeks: Int, allowWeekends: Boolean,
                                     wateringDayFrequency :Long) :List<Instant> {

    // Create a list to hold the watering dates start with the start date
    var wateringDates = mutableListOf<Instant>()

    var validatedStartDate = createValidStartDate(startDate, allowWeekends)

    // create upper bound instant that is numWeeks from the start date
    var lastDate = validatedStartDate.plus(Duration.ofDays((NUMBER_OF_DAYS_IN_WEEK * numWeeks).toLong()))

    // Begin with the validated start date
    var currentWateringDate = validatedStartDate


    // Generate schedule up to and on the last date
    while (currentWateringDate.isBefore(lastDate) || currentWateringDate == lastDate) {
      // Add the current watering date to the schedule
      wateringDates.add(currentWateringDate)


      // Increment by how many days the plant has to be watered for (frequency)
      currentWateringDate = currentWateringDate.plus(Duration.ofDays(wateringDayFrequency))

      if (!allowWeekends) { // If weekends aren't allowed to be scheduled for
        // Get the day of week
        var currentScheduleDayOfWeek = currentWateringDate.atOffset(ZoneOffset.UTC).dayOfWeek
        if (currentScheduleDayOfWeek == DayOfWeek.SUNDAY) {
          // If it is on Sunday then water it a day after on Monday
          currentWateringDate.plus(Duration.ofDays(1))
        } else if (currentScheduleDayOfWeek == DayOfWeek.SATURDAY) {
          // If it is on Saturday then water it a day early on Friday
          currentWateringDate.minus(Duration.ofDays(1))
        }

      }


    }

    // Return a list of the watering dates
    return wateringDates.toList()

  }

  /**
   * Returns a valid start date, if no weekends are allowed then the start date is advanced to the following Monday
   *
   * @param startDate The requested start date of the schedule
   * @param allowWeekends Whether to allow schedules to have watering dates on weekends or not
   *
   * @return  A start date that conforms to restrictions placed on it
   */
  private suspend fun createValidStartDate(startDate :Instant, allowWeekends:Boolean) :Instant {

    // Set new start date to the original start date by default
    var newStartDate = startDate
    // Check to see if the start date is on a weekend and weekends aren't allowed
    // If this is the case switch it to the following Monday as the start date
    if (!allowWeekends) {
      val currentDayOfWeek = newStartDate.atOffset(ZoneOffset.UTC).dayOfWeek
      if (currentDayOfWeek == DayOfWeek.SATURDAY) {
        // Advance to monday by going ahead two days
        newStartDate = newStartDate.plus(Duration.ofDays(2))
      } else if (currentDayOfWeek == DayOfWeek.SUNDAY) {
        // Advance to monday by going ahead one day
        newStartDate = newStartDate.plus(Duration.ofDays(1))
      }
    }

    return newStartDate
  }

  override suspend fun createPlant(newPlant: Plant): Plant {
    try {
      if (newPlant.waterEveryNumDays < 0 ) {
        throw Exception() // TODO: Replace with invalid water num days
      }

      // Call to the create plant repo
      return plantObjectRepo.createPlant(newPlant)
    } catch (throwable :Throwable) {
        when(throwable) {
          else -> throw throwable
        }
    }
  }

  override suspend fun removePlantById(id: String): Plant {
    try {
      // Delete the plant
      return plantObjectRepo.removePlantById(id);
    } catch (throwable :Throwable) {
      when (throwable) {
        else -> throw throwable
      }
    }
  }
}
