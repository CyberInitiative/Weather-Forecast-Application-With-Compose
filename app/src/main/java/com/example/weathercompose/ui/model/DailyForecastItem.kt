package com.example.weathercompose.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class DailyForecastItem(
    val date: String,
    val dayOfMonth: String,
    @DrawableRes
    val weatherIconRes: Int,
    @StringRes
    val weatherDescription: Int,
    val maxTemperature: Int,
    val minTemperature: Int,
)