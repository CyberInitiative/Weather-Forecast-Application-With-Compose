package com.example.weathercompose.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weathercompose.data.database.dao.CityDao
import com.example.weathercompose.data.database.entity.city.CityEntity
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity

@Database(
    entities = [CityEntity::class, DailyForecastEntity::class, HourlyForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherForecastDatabase : RoomDatabase() {
    abstract fun cities(): CityDao

    companion object {
        const val DATABASE_NAME = "weather_forecast.db"
    }
}