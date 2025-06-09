package com.example.weathercompose.domain.model.location

import com.example.weathercompose.domain.model.forecast.WeatherDescription

data class WidgetLocation(
    val locationId: Long,
    val locationName: String,
    val dailyMaxTemperature: Double,
    val dailyMinTemperature: Double,
    val weatherDescription: WeatherDescription,
)