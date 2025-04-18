package com.example.weathercompose.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.weathercompose.data.database.entity.combined.LocationWithDailyForecasts
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
import com.example.weathercompose.data.database.entity.location.LocationEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Dao
abstract class LocationDao {

    @Transaction
    @Query("SELECT * FROM locations")
    abstract suspend fun loadAll(): List<LocationWithDailyForecasts>

    @Transaction
    @Query("SELECT * FROM locations WHERE locationId = :locationId")
    abstract suspend fun load(locationId: Long): LocationWithDailyForecasts

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(location: LocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(dailyForecast: DailyForecastEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(hourlyForecast: HourlyForecastEntity): Long

    @Query("DELETE FROM locations WHERE locationId = :locationId")
    abstract suspend fun deleteLocationById(locationId: Long)

    @Delete
    abstract suspend fun delete(location: LocationEntity)

    @Delete
    abstract suspend fun delete(dailyForecasts: DailyForecastEntity)

    @Delete
    abstract suspend fun delete(hourlyForecasts: HourlyForecastEntity)

    @Update
    abstract suspend fun update(location: LocationEntity)

    @Update
    abstract suspend fun update(dailyForecasts: DailyForecastEntity)

    @Update
    abstract suspend fun update(dailyForecasts: HourlyForecastEntity)

    @Query("DELETE FROM daily_forecasts WHERE locationId = :locationId")
    abstract suspend fun deleteDailyForecastsByLocationId(locationId: Long)

    @Query("DELETE FROM hourly_forecasts WHERE dailyForecastId = :dailyForecasts")
    abstract suspend fun deleteHourlyForecastsByDailyForecastId(dailyForecasts: Long)

    @Query("SELECT * FROM daily_forecasts WHERE locationId = :locationId")
    abstract suspend fun findDailyForecastsByLocationId(locationId: Long): List<DailyForecastEntity>

    @Transaction
    open suspend fun saveForecasts(dailyForecasts: List<DailyForecastEntity>) = coroutineScope {
        dailyForecasts.map { dailyForecastEntity ->
            async {
                val dailyForecastEntityId = insert(dailyForecast = dailyForecastEntity)

                dailyForecastEntity.hourlyForecasts.map { hourlyForecastEntity ->
                    async {
                        insert(
                            hourlyForecast = hourlyForecastEntity.copy(
                                dailyForecastId = dailyForecastEntityId
                            )
                        )
                    }
                }
            }
        }
    }

    @Query("SELECT COUNT(*) FROM daily_forecasts")
    abstract suspend fun countDailyForecasts(): Int

    @Query("SELECT COUNT(*) FROM hourly_forecasts")
    abstract suspend fun countHourlyForecasts(): Int

}