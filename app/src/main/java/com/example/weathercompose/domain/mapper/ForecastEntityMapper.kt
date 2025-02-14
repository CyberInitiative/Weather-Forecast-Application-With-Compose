package com.example.weathercompose.domain.mapper

import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel

const val NO_DAILY_FORECAST_ID_SET = -1L

fun DailyForecastDomainModel.mapToEntity(cityId: Long): DailyForecastEntity {
    val dailyForecast = DailyForecastEntity(
        cityId = cityId,
        date = date,
        weatherDescription = weatherDescription,
        maxTemperature = maxTemperature,
        minTemperature = minTemperature,
        sunrise = sunrise,
        sunset = sunset,
    )
    dailyForecast.hourlyForecasts = hourlyForecasts.map { it.mapToEntity() }
    return dailyForecast
}

fun HourlyForecastDomainModel.mapToEntity(): HourlyForecastEntity {
    return HourlyForecastEntity(
        dailyForecastId = NO_DAILY_FORECAST_ID_SET,
        date = date,
        hour = hour,
        weatherDescription = weatherDescription,
        temperature = temperature,
        isDay = isDay
    )
}