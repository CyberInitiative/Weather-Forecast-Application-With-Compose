package com.example.weathercompose.ui.mapper

import android.content.Context
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.HourlyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.ui.model.DailyForecastItem
import com.example.weathercompose.ui.model.HourlyForecastItem
import com.example.weathercompose.utils.dateStringToDayOfWeek
import com.example.weathercompose.utils.dateStringToMonthAndDayNumber

class ForecastMapper(private val context: Context) {

    fun mapForecast(forecasts: List<DailyForecastDomainModel>): List<DailyForecastItem> {
        return forecasts.map { it.mapToDailyForecastItem() }
    }

    private fun DailyForecastDomainModel.mapToDailyForecastItem(): DailyForecastItem {
        return DailyForecastItem(
            date = date,
            dayNameInWeek = dateStringToDayOfWeek(date),
            monthAndDayNumber = dateStringToMonthAndDayNumber(date),
            weatherIconRes = WeatherDescription.weatherDescriptionToIconRes(
                weatherDescription = weatherDescription,
            ),
            weatherDescription = WeatherDescription.weatherDescriptionToString(
                weatherDescription = weatherDescription,
            ),
            maxTemperature = Math.round(maxTemperature).toInt(),
            minTemperature = Math.round(minTemperature).toInt(),
            hourlyForecasts = hourlyForecasts.map { it.mapToHourlyForecastItem() }
        )
    }

    private fun HourlyForecastDomainModel.mapToHourlyForecastItem(): HourlyForecastItem {
        return HourlyForecastItem(
            date = date,
            hour = hour,
            formattedHour = "$hour:00",
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
}