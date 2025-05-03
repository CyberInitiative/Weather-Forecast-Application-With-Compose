package com.example.weathercompose.data.database.entity.forecast

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.domain.model.forecast.WeatherDescription

@Entity(
    tableName = "daily_forecasts",
    foreignKeys = [ForeignKey(
        entity = LocationEntity::class,
        parentColumns = arrayOf("locationId"),
        childColumns = arrayOf("locationId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )]
)
data class DailyForecastEntity(
    val locationId: Long,
    val date: String,
    val weatherDescription: WeatherDescription,
    val maxTemperature: Double,
    val minTemperature: Double,
    val sunrise: String,
    val sunset: String,
    val timestamp: Long,
    @PrimaryKey(autoGenerate = true)
    val dailyForecastId: Long = 0,
) {
    @Ignore
    var hourlyForecasts: List<HourlyForecastEntity> = emptyList()

    constructor(
        locationId: Long,
        date: String,
        weatherDescription: WeatherDescription,
        maxTemperature: Double,
        minTemperature: Double,
        sunrise: String,
        sunset: String,
        timestamp: Long,
        hourlyForecasts: List<HourlyForecastEntity>,
        dailyForecastId: Long = 0,
    ) : this(
        locationId,
        date,
        weatherDescription,
        maxTemperature,
        minTemperature,
        sunrise,
        sunset,
        timestamp,
        dailyForecastId
    ) {
        this.hourlyForecasts = hourlyForecasts
    }
}