package com.example.weathercompose.domain.mapper

import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel

fun DailyForecastDomainModel.mapToEntity(cityId: Long): DailyForecastEntity {
    return DailyForecastEntity(
        cityId = cityId,
        date = date,
        weatherDescription = weatherDescription,
        maxTemperature = maxTemperature,
        minTemperature = minTemperature,
        sunrise = sunrise,
        sunset = sunset,
    )
}

fun HourlyForecastDomainModel.mapToEntity(dailyForecastId: Long): HourlyForecastEntity {
    return HourlyForecastEntity(
        dailyForecastId = dailyForecastId,
        date = date,
        time = time,
        weatherDescription = weatherDescription,
        temperature = temperature,
        isDay = isDay
    )
}