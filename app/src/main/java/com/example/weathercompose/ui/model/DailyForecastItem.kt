package com.example.weathercompose.ui.model

import androidx.annotation.DrawableRes

data class DailyForecastItem(
    val date: String,
    val dayNameInWeek: String,
    val monthAndDayNumber: String,
    @DrawableRes
    val weatherIconRes: Int,
    val weatherDescription: String,
    val maxTemperature: Int,
    val minTemperature: Int,
    val hourlyForecasts: List<HourlyForecastItem>,
)