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

@Dao
interface CityDao {

    @Transaction
    @Query("SELECT * FROM cities")
    suspend fun loadAll(): List<CityWithDailyForecasts>

    @Transaction
    @Query("SELECT * FROM cities WHERE cityId = :cityId")
    suspend fun load(cityId: Long): CityWithDailyForecasts

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(city: CityEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dailyForecasts: DailyForecastEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(hourlyForecasts: HourlyForecastEntity): Long

    @Delete
    suspend fun delete(city: CityEntity)

    @Delete
    suspend fun delete(dailyForecasts: DailyForecastEntity)

    @Delete
    suspend fun delete(hourlyForecasts: HourlyForecastEntity)

    @Update
    suspend fun update(city: CityEntity)

    @Update
    suspend fun update(dailyForecasts: DailyForecastEntity)

    @Update
    suspend fun update(dailyForecasts: HourlyForecastEntity)
}