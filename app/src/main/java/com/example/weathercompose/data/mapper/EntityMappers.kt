package com.example.weathercompose.data.mapper

import com.example.weathercompose.data.database.entity.combined.DailyForecastWithHourlyForecast
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
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
            hourlyForecastItem.mapToHourlyForecastDomainModel()
        }
    )
}

fun DailyForecastEntity.mapToHourlyForecastDomainModel(): DailyForecastDomainModel {
    return DailyForecastDomainModel(
        date = LocalDate.parse(date),
        weatherDescription = weatherDescription,
        maxTemperature = maxTemperature,
        minTemperature = minTemperature,
        sunrise = sunrise,
        sunset = sunset,
        hourlyForecasts = emptyList()
    )
}

fun HourlyForecastEntity.mapToHourlyForecastDomainModel(): HourlyForecastDomainModel {
    return HourlyForecastDomainModel(
        date = LocalDate.parse(date),
        time = LocalTime.parse(time),
        temperature = temperature,
        relativeHumidity = relativeHumidity,
        precipitationProbability = precipitationProbability,
        weatherDescription = weatherDescription,
        windSpeed = windSpeed,
        isDay = isDay,
    )
}