package com.example.weathercompose.domain.mapper

import com.example.weathercompose.data.database.entity.combined.DailyForecastWithHourlyForecast
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel

fun DailyForecastWithHourlyForecast.mapToDailyForecastDomainModel(): DailyForecastDomainModel {
    return DailyForecastDomainModel(
        date = dailyForecastEntity.date,
        weatherDescription = dailyForecastEntity.weatherDescription,
        maxTemperature = dailyForecastEntity.maxTemperature,
        minTemperature = dailyForecastEntity.minTemperature,
        sunrise = dailyForecastEntity.sunrise,
        sunset = dailyForecastEntity.sunset,
        hourlyForecasts = hourlyForecasts.map { hourlyForecastItem ->
            HourlyForecastDomainModel(
                date = hourlyForecastItem.date,
                hour = hourlyForecastItem.hour,
                weatherDescription = hourlyForecastItem.weatherDescription,
                temperature = hourlyForecastItem.temperature,
                isDay = hourlyForecastItem.isDay,
            )
        }
    )
}