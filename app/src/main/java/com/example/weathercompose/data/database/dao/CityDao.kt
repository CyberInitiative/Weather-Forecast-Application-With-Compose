package com.example.weathercompose.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.weathercompose.data.database.entity.city.CityEntity
import com.example.weathercompose.data.database.entity.combined.CityWithDailyForecasts
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@Dao
abstract class CityDao {

    @Transaction
    @Query("SELECT * FROM cities")
    abstract suspend fun loadAll(): List<CityWithDailyForecasts>

    @Transaction
    @Query("SELECT * FROM cities WHERE cityId = :cityId")
    abstract suspend fun load(cityId: Long): CityWithDailyForecasts

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(city: CityEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(dailyForecast: DailyForecastEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(hourlyForecast: HourlyForecastEntity): Long

    @Delete
    abstract suspend fun delete(city: CityEntity)

    @Delete
    abstract suspend fun delete(dailyForecasts: DailyForecastEntity)

    @Delete
    abstract suspend fun delete(hourlyForecasts: HourlyForecastEntity)

    @Update
    abstract suspend fun update(city: CityEntity)

    @Update
    abstract suspend fun update(dailyForecasts: DailyForecastEntity)

    @Update
    abstract suspend fun update(dailyForecasts: HourlyForecastEntity)

    @Query("DELETE FROM daily_forecasts WHERE cityId = :cityId")
    abstract suspend fun deleteDailyForecastsByCityId(cityId: Long)

    @Query("DELETE FROM hourly_forecasts WHERE dailyForecastId = :dailyForecasts")
    abstract suspend fun deleteHourlyForecastsByDailyForecastId(dailyForecasts: Long)

    @Query("SELECT * FROM daily_forecasts WHERE cityId = :cityId")
    abstract suspend fun findDailyForecastsByCityId(cityId: Long): List<DailyForecastEntity>

    @Transaction
    open suspend fun deleteForecastsFromCity(cityId: Long) {
        val dailyForecasts = findDailyForecastsByCityId(cityId = cityId)
        for (entity in dailyForecasts) {
            deleteHourlyForecastsByDailyForecastId(dailyForecasts = entity.dailyForecastId)
        }
        deleteDailyForecastsByCityId(cityId = cityId)
    }

    @Transaction
    open suspend fun saveForecasts(dailyForecasts: List<DailyForecastEntity>) = coroutineScope {
        dailyForecasts.map { dailyForecastEntity ->
            async {
                val dailyForecastEntityId = insert(dailyForecast = dailyForecastEntity)

                if (dailyForecastEntity.hourlyForecasts.isNotEmpty()) {
                    dailyForecastEntity.hourlyForecasts.map { hourlyForecastEntity ->
                        async {
                            insert(hourlyForecast = hourlyForecastEntity.copy(dailyForecastId = dailyForecastEntityId))
                        }
                    }.awaitAll()
                }
            }
        }.awaitAll()
    }


}