package com.example.weathercompose.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HourlyForecastItem(
    val time: String,
    @DrawableRes
    val weatherIconRes: Int,
    @StringRes
    val weatherDescription: Int,
    val precipitationProbability: String,
    val temperature: String,
)