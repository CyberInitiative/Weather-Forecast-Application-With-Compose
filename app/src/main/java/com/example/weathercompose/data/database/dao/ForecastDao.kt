package com.example.weathercompose.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.weathercompose.data.database.entity.combined.DailyForecastWithHourlyForecast
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ForecastDao {

    @Transaction
    @Query("SELECT * FROM daily_forecasts")
    abstract fun loadAllDailyForecastsWithHourlyForecasts(): Flow<List<DailyForecastWithHourlyForecast>>

    @Transaction
    @Query("SELECT * FROM daily_forecasts WHERE locationId = :locationId")
    abstract suspend fun getForecastsByLocationID(
        locationId: Long,
    ): List<DailyForecastWithHourlyForecast>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDailyForecast(dailyForecast: DailyForecastEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertHourlyForecast(hourlyForecast: HourlyForecastEntity): Long

    @Query("DELETE FROM daily_forecasts WHERE locationId = :locationId")
    abstract suspend fun deleteDailyForecastsByLocationId(locationId: Long)

    @Query(
        """
        DELETE FROM hourly_forecasts
        WHERE dailyForecastId IN (
            SELECT dailyForecastId FROM daily_forecasts
            WHERE dailyForecastId = :dailyForecastId
            AND locationId = :locationId
        )
    """
    )
    abstract suspend fun deleteHourlyForecastsByLocationIdAndDailyForecastId(
        locationId: Long,
        dailyForecastId: Long,
    )

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
                    hourlyForecastEntity.copy(dailyForecastId = dailyForecastEntityId)
                )
            }
        }
    }
}