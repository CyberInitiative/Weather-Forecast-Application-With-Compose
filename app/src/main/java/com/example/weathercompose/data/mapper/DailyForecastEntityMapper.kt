package com.example.weathercompose.data.mapper

import com.example.weathercompose.data.database.entity.combined.DailyForecastWithHourlyForecast
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun DailyForecastWithHourlyForecast.mapToDailyForecastDomainModel(): DailyForecastDomainModel {
    return DailyForecastDomainModel(
        date = LocalDate.parse(dailyForecastEntity.date),
        weatherDescription = dailyForecastEntity.weatherDescription,
        maxTemperature = dailyForecastEntity.maxTemperature,
        minTemperature = dailyForecastEntity.minTemperature,
        sunrise = LocalDateTime.parse(dailyForecastEntity.sunrise).toLocalTime().toString(),
        sunset = LocalDateTime.parse(dailyForecastEntity.sunset).toLocalTime().toString(),
        hourlyForecasts = hourlyForecasts.map { hourlyForecastItem ->
            HourlyForecastDomainModel(
                date = LocalDate.parse(hourlyForecastItem.date),
                time = LocalTime.parse(hourlyForecastItem.time),
                temperature = hourlyForecastItem.temperature,
                relativeHumidity = hourlyForecastItem.relativeHumidity,
                precipitationProbability = hourlyForecastItem.precipitationProbability,
                weatherDescription = hourlyForecastItem.weatherDescription,
                windSpeed = hourlyForecastItem.windSpeed,
                isDay = hourlyForecastItem.isDay,
            )
        }
    )
}