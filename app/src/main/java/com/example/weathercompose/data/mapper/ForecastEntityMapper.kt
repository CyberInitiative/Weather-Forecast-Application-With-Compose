package com.example.weathercompose.data.mapper

import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel

const val NO_DAILY_FORECAST_ID_SET = 0L

//fun DailyForecastDomainModel.mapToDailyForecastEntity(locationId: Long): DailyForecastEntity {
//    val dailyForecast = DailyForecastEntity(
//        locationId = locationId,
//        date = date.toString(),
//        weatherDescription = weatherDescription,
//        maxTemperature = maxTemperature,
//        minTemperature = minTemperature,
//        sunrise = sunrise,
//        sunset = sunset,
//    )
//    dailyForecast.hourlyForecasts = hourlyForecasts.map { it.mapToHourlyForecastEntity() }
//    return dailyForecast
//}

fun HourlyForecastDomainModel.mapToHourlyForecastEntity(): HourlyForecastEntity {
    return HourlyForecastEntity(
        dailyForecastId = NO_DAILY_FORECAST_ID_SET,
        date = date.toString(),
        time = time.toString(),
        weatherDescription = weatherDescription,
        temperature = temperature,
        isDay = isDay
    )
}