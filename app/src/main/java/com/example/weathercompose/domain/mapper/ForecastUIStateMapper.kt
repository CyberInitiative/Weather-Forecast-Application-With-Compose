package com.example.weathercompose.domain.mapper

import android.content.Context
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.ui.ui_state.LocationForecastUIState
import com.example.weathercompose.utils.getCurrentDateInTimeZone
import java.time.format.TextStyle
import java.util.Locale

class ForecastUIStateMapper(private val context: Context) {
    fun mapToUIState(location: LocationDomainModel): LocationForecastUIState {
        with(location) {
            val currentHourlyForecast = location.getForecastForCurrentHour()
            val currentDayForecast = forecasts[0]
            val weatherDescription =
                WeatherDescription.weatherDescriptionToString(currentHourlyForecast.weatherDescription)
            val currentDayMaxTemperature = Math.round(currentDayForecast.maxTemperature).toInt()
            val currentDayMinTemperature = Math.round(currentDayForecast.minTemperature).toInt()

            return LocationForecastUIState.LocationDataUIState(
                locationName = name,
                currentHourTemperature = "${
                    Math.round(currentHourlyForecast.temperature).toInt()
                }°",
                currentDayMaxTemperature = "$currentDayMaxTemperature°",
                currentDayMinTemperature = "$currentDayMinTemperature°",
                currentHourWeatherStatus = context.getString(weatherDescription),
                currentDayOfWeekAndDate = getCurrentDayMaxAndMinTemperature(timeZone = timeZone),
                currentWeatherIcon = WeatherDescription.weatherDescriptionToIconRes(
                    weatherDescription = currentHourlyForecast.weatherDescription,
                ),
                dailyForecastsUIState = mapToDailyForecastDataUIState(
                    dailyForecastItems = forecasts/*.drop(1)*/
                        .map { it.mapToDailyForecastItem(timeZone = location.timeZone) },
                    timeZone = location.timeZone,
                ),
                hourlyForecastsUIState = mapToHourlyForecastDataUIState(
                    hourlyForecastItems = getForecastFor24Hours()
                        .map { it.mapToHourlyForecastItem() },
                    timeZone = location.timeZone,
                )
            )
        }
    }

    private fun getCurrentDayMaxAndMinTemperature(timeZone: String): String {
        val currentDateInTimeZone = getCurrentDateInTimeZone(timeZone = timeZone)
        val dayOfWeek = currentDateInTimeZone.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US)
        val date = "${currentDateInTimeZone.dayOfMonth} ${
            currentDateInTimeZone.month.getDisplayName(
                TextStyle.SHORT,
                Locale.US
            )
        }"
        return "$dayOfWeek, $date"
    }
}
