package com.example.weathercompose.data.model.widget

import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.domain.model.forecast.WeatherDescription.Companion.isWeatherWithPrecipitationsOrOvercast
import com.example.weathercompose.ui.model.WeatherAndDayTimeState

data class WidgetLocationWithForecasts(
    val locationId: Long,
    val locationName: String,
    val dailyMaxTemperature: Double,
    val dailyMinTemperature: Double,
    val weatherDescription: WeatherDescription,
    val hourlyForecasts: List<WidgetHourlyForecast>,
) {
    fun getPrecipitationsAndTimeOfDayStateForCurrentHour(): WeatherAndDayTimeState {
        val currentHour = hourlyForecasts.firstOrNull()

        return when {
            currentHour == null -> WeatherAndDayTimeState.NO_PRECIPITATION_DAY

            currentHour.isDay && !currentHour.weatherDescription
                .isWeatherWithPrecipitationsOrOvercast() -> {
                WeatherAndDayTimeState.NO_PRECIPITATION_DAY
            }

            !currentHour.isDay && !currentHour.weatherDescription
                .isWeatherWithPrecipitationsOrOvercast() -> {
                WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT
            }

            currentHour.isDay && currentHour.weatherDescription
                .isWeatherWithPrecipitationsOrOvercast() -> {
                WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY
            }

            !currentHour.isDay && currentHour.weatherDescription
                .isWeatherWithPrecipitationsOrOvercast() -> {
                WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT
            }

            else -> WeatherAndDayTimeState.NO_PRECIPITATION_DAY
        }
    }
}