package com.example.weathercompose.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weathercompose.data.database.dao.CityDao
import com.example.weathercompose.data.database.entity.CityEntity

@Database(entities = [CityEntity::class], version = 1)
abstract class WeatherForecastDatabase : RoomDatabase() {
    abstract fun cities(): CityDao

    companion object{
        const val DATABASE_NAME = "weather_forecast.db"
    }
}