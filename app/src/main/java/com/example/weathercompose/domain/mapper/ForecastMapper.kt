package com.example.weathercompose.domain.mapper

import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.domain.model.forecast.WeatherDescription.Companion.isWeatherWithPrecipitations
import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.utils.getCurrentDateInTimeZone
import com.example.weathercompose.utils.getCurrentHourInTimeZone
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

fun DailyForecastDomainModel.mapToDailyForecastItem(
    timeZone: String,
    temperatureUnit: TemperatureUnit,
): DailyForecastItem {
    return DailyForecastItem(
        date = getDayOfWeek(date = this.date, timeZone = timeZone),
        dayOfMonth = "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.SHORT, Locale.US)}",
        weatherIconRes = WeatherDescription.weatherDescriptionToIconRes(
            weatherDescription = weatherDescription,
        ),
        weatherDescription = WeatherDescription.weatherDescriptionToString(
            weatherDescription = weatherDescription,
        ),
        maxTemperature = TemperatureUnit.getTemperatureForUI(
            temperature = maxTemperature,
            temperatureUnit = temperatureUnit
        ),
        minTemperature = TemperatureUnit.getTemperatureForUI(
            temperature = minTemperature,
            temperatureUnit = temperatureUnit
        ),
    )
}

private fun getDayOfWeek(date: LocalDate, timeZone: String): String {
    val currentDateInTimeZone = getCurrentDateInTimeZone(timeZone = timeZone)
    return if (currentDateInTimeZone == date) {
        "Today"
    } else {
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
    }
}

fun HourlyForecastDomainModel.mapToHourlyForecastItem(
    timeZone: String,
    temperatureUnit: TemperatureUnit
): HourlyForecastItem {
    return HourlyForecastItem(
        time = getTime(time, timeZone),
        weatherIconRes = WeatherDescription.weatherDescriptionToIconRes(
            weatherDescription = weatherDescription,
            isDay = isDay,
        ),
        weatherDescription = WeatherDescription.weatherDescriptionToString(
            weatherDescription = weatherDescription,
        ),
        temperature = TemperatureUnit.getTemperatureForUI(
            temperature = temperature,
            temperatureUnit = temperatureUnit
        ),
        precipitationProbability = getPrecipitationProbability(
            precipitationProbability = precipitationProbability,
            isWeatherWithPrecipitations = weatherDescription.isWeatherWithPrecipitations()
        )
    )
}

private fun getTime(time: LocalTime, timeZone: String): String {
    val currentDateInTimeZone = getCurrentHourInTimeZone(timeZone = timeZone)
    return if (currentDateInTimeZone == time) {
        "Now"
    } else {
        time.toString()
    }
}

private fun getPrecipitationProbability(
    precipitationProbability: Int,
    isWeatherWithPrecipitations: Boolean
): String {
    return if (isWeatherWithPrecipitations) {
        "$precipitationProbability%"
    } else {
        ""
    }
}