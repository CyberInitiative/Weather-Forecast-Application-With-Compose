package com.example.weathercompose.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.time.LocalDate

data class DailyForecastItem(
    val date: LocalDate,
    @DrawableRes
    val weatherIconRes: Int,
    @StringRes
    val weatherDescription: Int,
    val maxTemperature: Int,
    val minTemperature: Int,
)