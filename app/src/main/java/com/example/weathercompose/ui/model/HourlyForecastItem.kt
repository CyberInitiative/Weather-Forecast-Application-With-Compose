package com.example.weathercompose.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.time.LocalDate

data class HourlyForecastItem(
    val time: String,
    val date: LocalDate,
    @DrawableRes
    val weatherIconRes: Int,
    @StringRes
    val weatherDescription: Int,
    val precipitationProbability: Int?,
    val temperature: Int,
)