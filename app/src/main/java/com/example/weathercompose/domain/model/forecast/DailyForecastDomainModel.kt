package com.example.weathercompose.domain.model.forecast

import java.time.LocalDate

data class DailyForecastDomainModel(
    val date: LocalDate,
    val weatherDescription: WeatherDescription,
    val maxTemperature: Double,
    val minTemperature: Double,
    val sunrise: String,
    val sunset: String,
    val hourlyForecasts: List<HourlyForecastDomainModel>
)