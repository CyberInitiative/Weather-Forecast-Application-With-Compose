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

//private fun getForecastFor24Hours(cityDomainModel: CityDomainModel): List<HourlyForecastItem> {
//    val currentDateAndTime = getCurrentDateAndTimeInTimeZone(cityDomainModel.timeZone)
//    val nextDateFromCurrent = getNextDateFromCurrent(cityDomainModel.timeZone)
//    val forecastForCurrentDate =
//        cityDomainModel.forecasts.firstOrNull { it.date == currentDateAndTime.date }
//    val forecastForNextDate =
//        cityDomainModel.forecasts.firstOrNull { it.date == nextDateFromCurrent }
//
//    val hourlyForecastsForCurrentDay = forecastForCurrentDate?.hourlyForecasts ?: emptyList()
//    val hourlyForecastsForNextDay = forecastForNextDate?.hourlyForecasts ?: emptyList()
//
//    val combinedForecasts = hourlyForecastsForCurrentDay + hourlyForecastsForNextDay
//
//    if (combinedForecasts.isEmpty()) {
//        return emptyList()
//    } else {
////         combinedForecasts.filter {  }
//    }
//    return emptyList()
//}