package com.example.weathercompose.domain.model.forecast

import java.time.LocalDate
import java.time.LocalTime

data class HourlyForecastDomainModel(
    val date: LocalDate,
    val time: LocalTime,
    val weatherDescription: WeatherDescription,
    val temperature: Double,
    val isDay: Boolean,
)