package com.example.weathercompose.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.time.LocalDate
import java.time.LocalTime

data class HourlyForecastItem(
    val time: LocalTime,
    val date: LocalDate,
    @DrawableRes
    val weatherIconRes: Int,
    @StringRes
    val weatherDescription: Int,
    val temperature: Int,
)