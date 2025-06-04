package com.example.weathercompose.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.weathercompose.data.database.entity.combined.DailyForecastWithHourlyForecast
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity

@Dao
abstract class ForecastDao {

    /*
    @Transaction
    @Query("SELECT * FROM daily_forecasts")
    abstract fun findAllDailyForecastsWithHourlyForecasts(): Flow<List<DailyForecastWithHourlyForecast>>
     */

    @Transaction
    @Query("SELECT * FROM daily_forecasts WHERE locationId = :locationId")
    abstract suspend fun findDailyForecastsWithHourlyForecastsByLocationId(
        locationId: Long,
    ): List<DailyForecastWithHourlyForecast>

    @Query("""
        SELECT * FROM daily_forecasts 
        WHERE locationId = :locationId AND
        date = :date
        """
    )
    abstract suspend fun findDailyForecastByLocationIdAndDate(
        locationId: Long,
        date: String,
    ): DailyForecastEntity?

    @Query("SELECT * FROM hourly_forecasts WHERE locationId =:locationId LIMIT :limit")
    abstract suspend fun findHourlyForecastsByLocationId(
        locationId: Long,
        limit: Int
    ): List<HourlyForecastEntity>

    @Query("SELECT * FROM hourly_forecasts WHERE locationId =:locationId")
    abstract suspend fun findHourlyForecastsByLocationId(
        locationId: Long,
    ): List<HourlyForecastEntity>

    @Query(
        """
    SELECT * FROM hourly_forecasts
    WHERE locationId = :locationId 
      AND (
            (date = :date AND time >= :startTime) 
            OR (date > :date) 
          )
    ORDER BY date, time
    LIMIT :limit
    """
    )
    abstract suspend fun findHourlyForecastsByLocationIdAndDateAndStartHour(
        locationId: Long,
        date: String,
        startTime: String,
        limit: Int
    ): List<HourlyForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDailyForecast(dailyForecast: DailyForecastEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertHourlyForecast(hourlyForecast: HourlyForecastEntity): Long

    @Query("DELETE FROM daily_forecasts WHERE locationId = :locationId")
    abstract suspend fun deleteDailyForecastsByLocationId(locationId: Long)

    @Transaction
    open suspend fun saveForecasts(
        locationId: Long,
        dailyForecasts: List<DailyForecastEntity>
    ) {
        deleteDailyForecastsByLocationId(locationId = locationId)

        for (dailyForecastEntity in dailyForecasts) {
            val dailyForecastEntityId = insertDailyForecast(dailyForecastEntity)

            for (hourlyForecastEntity in dailyForecastEntity.hourlyForecasts) {
                insertHourlyForecast(
                    hourlyForecastEntity.copy(
                        dailyForecastId = dailyForecastEntityId,
                        locationId = locationId,
                    )
                )
            }
        }
    }
}