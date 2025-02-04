package com.example.weathercompose.domain.model.forecast

data class DailyForecastDomainModel(
    val date: String,
    val weatherDescription: WeatherDescription,
    val maxTemperature: Double,
    val minTemperature: Double,
    val sunrise: String,
    val sunset: String,
    val hourlyForecastDomainModelData: List<HourlyForecastDomainModel>
)