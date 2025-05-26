package com.example.weathercompose.domain.mapper

import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.utils.getCurrentDateInTimeZone
import java.time.LocalDate
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
        maxTemperature = TemperatureUnit.getTemperature(
            temperature = maxTemperature,
            temperatureUnit = temperatureUnit
        ),
        minTemperature = TemperatureUnit.getTemperature(
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
    temperatureUnit: TemperatureUnit
): HourlyForecastItem {
    val precipitationProbability = if (isWeatherWithPrecipitations()) {
        precipitationProbability
    } else {
        null
    }

    return HourlyForecastItem(
        time = time,
        date = date,
        weatherIconRes = WeatherDescription.weatherDescriptionToIconRes(
            weatherDescription = weatherDescription,
            isDay = isDay,
        ),
        weatherDescription = WeatherDescription.weatherDescriptionToString(
            weatherDescription = weatherDescription,
        ),
        temperature = TemperatureUnit.getTemperature(
            temperature = temperature,
            temperatureUnit = temperatureUnit
        ),
        precipitationProbability = precipitationProbability
    )
}