package com.example.weathercompose.domain.model.forecast

data class HourlyForecastDomainModel(
    val date: String,
    val hour: String,
    val weatherDescription: WeatherDescription,
    val temperature: Double,
    val isDay: Boolean,
)