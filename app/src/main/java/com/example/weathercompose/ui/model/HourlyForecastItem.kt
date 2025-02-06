package com.example.weathercompose.ui.model

import androidx.annotation.DrawableRes

data class HourlyForecastItem(
    val date: String,
    val time: String,
    val formattedTime: String,
    @DrawableRes
    val weatherIconRes: Int,
    val weatherDescription: String,
    val temperature: Int,
)