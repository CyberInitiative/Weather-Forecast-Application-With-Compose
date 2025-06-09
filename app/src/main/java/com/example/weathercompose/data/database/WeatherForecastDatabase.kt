package com.example.weathercompose.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weathercompose.data.database.dao.ForecastDao
import com.example.weathercompose.data.database.dao.LocationDao
import com.example.weathercompose.data.database.dao.WidgetDao
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
import com.example.weathercompose.data.database.entity.location.LocationEntity

@Database(
    entities = [LocationEntity::class, DailyForecastEntity::class, HourlyForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherForecastDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun forecastDao(): ForecastDao
    abstract fun widgetDao(): WidgetDao

    companion object {
        const val DATABASE_NAME = "weather_forecast.db"
    }
}