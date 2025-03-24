package com.example.weathercompose.ui.mapper

import android.content.Context
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.ui.ui_state.CityForecastUIState

class ForecastUIStateMapper(private val context: Context) {
    fun mapToUIState(city: CityDomainModel): CityForecastUIState {
        with(city) {
            val currentHourlyForecast = city.getForecastForCurrentHour()
            val currentDayForecast = forecasts[0]
            val weatherDescription =
                WeatherDescription.weatherDescriptionToIconRes(currentDayForecast.weatherDescription)
            val currentDayMaxTemperature = Math.round(currentDayForecast.maxTemperature).toInt()
            val currentDayMinTemperature = Math.round(currentDayForecast.minTemperature).toInt()

            return CityForecastUIState.CityDataUIState(
                cityName = name,
                currentHourTemperature = "${
                    Math.round(currentHourlyForecast.temperature).toInt()
                }°",
                currentDayMaxAndMinTemperature =
                "$currentDayMaxTemperature° / $currentDayMinTemperature°",
                currentDayWeatherStatus = context.getString(weatherDescription),
                dailyForecastsUIState = mapToDailyForecastDataUIState(
                    dailyForecastItems = forecasts.drop(1).map { it.mapToDailyForecastItem() },
                    timeZone = city.timeZone,
                ),
                hourlyForecastsUIState = mapToHourlyForecastDataUIState(
                    hourlyForecastItems = getForecastFor24Hours()
                        .map { it.mapToHourlyForecastItem() },
                    timeZone = city.timeZone,
                )
            )
        }
    }
}
