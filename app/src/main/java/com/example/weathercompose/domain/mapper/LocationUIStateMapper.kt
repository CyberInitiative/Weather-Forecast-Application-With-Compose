package com.example.weathercompose.domain.mapper

import android.content.Context
import com.example.weathercompose.domain.model.DataState
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.ui.ui_state.LocationUIState
import com.example.weathercompose.utils.getCurrentDateInTimeZone
import java.time.format.TextStyle
import java.util.Locale

class LocationUIStateMapper(private val context: Context) {

    fun mapToUIState(location: LocationDomainModel): LocationUIState {
        return when (location.forecastDataState) {
            DataState.Initial,
            DataState.Loading -> onInitialOrLoadingState(location = location)

            is DataState.Ready -> onReadyState(location = location)

            is DataState.Error -> onError(location = location)

            DataState.NoData -> onNoData(location = location)
        }
    }

    private fun onInitialOrLoadingState(location: LocationDomainModel): LocationUIState {
        return LocationUIState(
            locationName = location.name,
            loading = true,
        )
    }

    private fun onReadyState(location: LocationDomainModel): LocationUIState {
        val forecasts =
            (location.forecastDataState as DataState.Ready<List<DailyForecastDomainModel>>).data
        val currentHourlyForecast = location.getForecastForCurrentHour()
        val currentDayForecast = forecasts[0]
        val weatherDescription = WeatherDescription.weatherDescriptionToString(
            weatherDescription = currentHourlyForecast.weatherDescription
        )
        val currentDayMaxTemperature = Math.round(currentDayForecast.maxTemperature).toInt()
        val currentDayMinTemperature = Math.round(currentDayForecast.minTemperature).toInt()
        return LocationUIState(
            locationName = location.name,
            currentHourTemperature = "${Math.round(currentHourlyForecast.temperature).toInt()}°",
            currentDayOfWeekAndDate = getCurrentDateAndDayOfWeek(location.timeZone),
            currentDayMaxTemperature = "$currentDayMaxTemperature°",
            currentDayMinTemperature = "$currentDayMinTemperature°",
            currentHourWeatherStatus = context.getString(weatherDescription),
            dailyForecasts = forecasts.map { it.mapToDailyForecastItem(location.timeZone) },
            hourlyForecasts = location.getForecastFor24Hours().map { it.mapToHourlyForecastItem() },
        )
    }

    private fun onError(location: LocationDomainModel): LocationUIState {
        val error = (location.forecastDataState as DataState.Error).error
        return LocationUIState(
            locationName = location.name,
            errorMessage = error,
        )
    }

    private fun onNoData(location: LocationDomainModel): LocationUIState {
        return LocationUIState(
            locationName = location.name,
        )
    }

    private fun getCurrentDateAndDayOfWeek(timeZone: String): String {
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
