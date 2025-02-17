package com.example.weathercompose.domain.mapper

import com.example.weathercompose.data.database.entity.combined.DailyForecastWithHourlyForecast
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import java.time.LocalDate
import java.time.LocalTime

fun DailyForecastWithHourlyForecast.mapToDailyForecastDomainModel(): DailyForecastDomainModel {
    return DailyForecastDomainModel(
        date = LocalDate.parse(dailyForecastEntity.date),
        weatherDescription = dailyForecastEntity.weatherDescription,
        maxTemperature = dailyForecastEntity.maxTemperature,
        minTemperature = dailyForecastEntity.minTemperature,
        sunrise = dailyForecastEntity.sunrise,
        sunset = dailyForecastEntity.sunset,
        hourlyForecasts = hourlyForecasts.map { hourlyForecastItem ->
            HourlyForecastDomainModel(
                date = LocalDate.parse(hourlyForecastItem.date),
                time = LocalTime.parse(hourlyForecastItem.time),
                weatherDescription = hourlyForecastItem.weatherDescription,
                temperature = hourlyForecastItem.temperature,
                isDay = hourlyForecastItem.isDay,
            )
        }
    )
}