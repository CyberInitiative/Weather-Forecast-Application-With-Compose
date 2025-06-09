package com.example.weathercompose.data.model.widget

import com.example.weathercompose.domain.model.forecast.WeatherDescription

data class WidgetLocationWithForecasts(
    val locationId: Long,
    val locationName: String,
    val dailyMaxTemperature: Double,
    val dailyMinTemperature: Double,
    val weatherDescription: WeatherDescription,
    val hourlyForecasts: List<WidgetHourlyForecast>,
)