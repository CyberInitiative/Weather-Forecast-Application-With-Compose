package com.example.weathercompose.data.model.widget

import com.example.weathercompose.domain.model.forecast.WeatherDescription

data class WidgetHourlyForecast(
    val time: String,
    val temperature: Double,
    val weatherDescription: WeatherDescription,
    val isDay: Boolean,
)