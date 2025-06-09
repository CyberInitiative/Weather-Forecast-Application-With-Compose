package com.example.weathercompose.data.model.widget

import com.example.weathercompose.domain.model.forecast.WeatherDescription

data class WidgetDailyForecast(
    val dailyMaxTemperature: Double,
    val dailyMinTemperature: Double,
    val weatherDescription: WeatherDescription,
)