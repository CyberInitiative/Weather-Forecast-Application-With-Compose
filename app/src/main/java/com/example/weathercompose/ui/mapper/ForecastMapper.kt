package com.example.weathercompose.ui.mapper

import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem

fun DailyForecastDomainModel.mapToDailyForecastItem(): DailyForecastItem {
    return DailyForecastItem(
        date = date,
        weatherIconRes = WeatherDescription.weatherDescriptionToIconRes(
            weatherDescription = weatherDescription,
        ),
        weatherDescription = WeatherDescription.weatherDescriptionToString(
            weatherDescription = weatherDescription,
        ),
        maxTemperature = Math.round(maxTemperature).toInt(),
        minTemperature = Math.round(minTemperature).toInt(),
    )
}

fun HourlyForecastDomainModel.mapToHourlyForecastItem(): HourlyForecastItem {
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
        temperature = Math.round(temperature).toInt(),
    )
}