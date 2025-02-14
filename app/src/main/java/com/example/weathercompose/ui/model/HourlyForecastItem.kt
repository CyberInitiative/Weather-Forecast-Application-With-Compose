package com.example.weathercompose.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HourlyForecastItem(
    val date: String,
    val hour: String,
    val formattedHour: String,
    @DrawableRes
    val weatherIconRes: Int,
    @StringRes
    val weatherDescription: Int,
    val temperature: Int,
)