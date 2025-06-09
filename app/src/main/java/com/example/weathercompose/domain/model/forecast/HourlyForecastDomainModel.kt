package com.example.weathercompose.domain.model.forecast

import java.time.LocalDate
import java.time.LocalTime

data class HourlyForecastDomainModel(
    val date: LocalDate,
    val time: LocalTime,
    val temperature: Double,
    val relativeHumidity: Int,
    val precipitationProbability: Int,
    val weatherDescription: WeatherDescription,
    val windSpeed: Double,
    val isDay: Boolean,
)