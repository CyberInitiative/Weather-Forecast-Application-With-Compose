package com.example.weathercompose.ui.model

data class HourlyForecastItem(
    val date: String,
    val time: String,
    val formattedTime: String,
    val weatherDescription: String,
    val temperature: Int,
)