package com.example.weathercompose.data.database.entity.forecast

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.weathercompose.domain.model.forecast.WeatherDescription

@Entity(
    tableName = "hourly_forecasts",
    foreignKeys = [ForeignKey(
        entity = DailyForecastEntity::class,
        parentColumns = arrayOf("dailyForecastId"),
        childColumns = arrayOf("dailyForecastId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )]
)
data class HourlyForecastEntity(
    val dailyForecastId: Long,
    val date: String,
    val time: String,
    val weatherDescription: WeatherDescription,
    val temperature: Double,
    val precipitationProbability: Int,
    val isDay: Boolean,
    @PrimaryKey(autoGenerate = true)
    val hourlyForecastId: Long = 0,
)