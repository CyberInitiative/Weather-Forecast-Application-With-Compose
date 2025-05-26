package com.example.weathercompose.domain.mapper

import android.content.Context
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.DataState
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.ui_state.LocationUIState
import com.example.weathercompose.utils.getCurrentDateInTimeZone
import java.time.format.TextStyle
import java.util.Locale

class LocationUIStateMapper(private val context: Context) {

    fun mapToUIState(
        location: LocationDomainModel,
        temperatureUnit: TemperatureUnit,
    ): LocationUIState {
        return when (location.forecastDataState) {
            DataState.Initial,
            DataState.Loading -> onInitialOrLoadingState(location = location)

            is DataState.Ready -> onReadyState(
                location = location,
                temperatureUnit = temperatureUnit
            )

            is DataState.Error -> onError(location = location)

            DataState.NoData -> onNoData(location = location)
        }
    }

    private fun onInitialOrLoadingState(location: LocationDomainModel): LocationUIState {
        return LocationUIState(
            id = location.id,
            locationName = location.name,
            isLoading = true,
        )
    }

    private fun onReadyState(
        location: LocationDomainModel,
        temperatureUnit: TemperatureUnit
    ): LocationUIState {
        val forecasts =
            (location.forecastDataState as DataState.Ready<List<DailyForecastDomainModel>>).data
        val currentHourlyForecast = location.getForecastForCurrentHour()
        val currentDayForecast = forecasts[0]
        val weatherDescription = WeatherDescription.weatherDescriptionToString(
            weatherDescription = currentHourlyForecast.weatherDescription
        )

        return LocationUIState(
            id = location.id,
            locationName = location.name,

            currentHourTemperature = TemperatureUnit.getTemperatureForUI(
                temperature = currentHourlyForecast.temperature,
                temperatureUnit = temperatureUnit
            ),
            currentDayOfWeekAndDate = getCurrentDateAndDayOfWeek(location.timeZone),
            currentDayMaxTemperature = TemperatureUnit.getTemperatureForUI(
                temperature = currentDayForecast.maxTemperature,
                temperatureUnit = temperatureUnit
            ),
            currentDayMinTemperature = TemperatureUnit.getTemperatureForUI(
                temperature = currentDayForecast.minTemperature,
                temperatureUnit = temperatureUnit
            ),

            currentHourWeatherStatus = context.getString(weatherDescription),
            dailyForecasts = forecasts.map {
                it.mapToDailyForecastItem(
                    timeZone = location.timeZone,
                    temperatureUnit = temperatureUnit
                )
            },
            hourlyForecasts = location.getForecastFor24Hours().map {
                it.mapToHourlyForecastItem(temperatureUnit = temperatureUnit)
            },
            weatherAndDayTimeState = getPrecipitationsAndTimeOfDayStateForCurrentHour(
                location = location
            )
        )
    }

    private fun onError(location: LocationDomainModel): LocationUIState {
        val error = (location.forecastDataState as DataState.Error).error
        return LocationUIState(
            id = location.id,
            locationName = location.name,
            errorMessage = error,
        )
    }

    private fun onNoData(location: LocationDomainModel): LocationUIState {
        return LocationUIState(
            id = location.id,
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

    private fun getPrecipitationsAndTimeOfDayStateForCurrentHour(
        location: LocationDomainModel
    ): WeatherAndDayTimeState {
        try {
            val hourlyForecast = location.getForecastForCurrentHour()

            return when {
                hourlyForecast.isDay && !hourlyForecast.isWeatherWithPrecipitationsOrOvercast()
                    -> {
                    WeatherAndDayTimeState.NO_PRECIPITATION_DAY
                }

                !hourlyForecast.isDay && !hourlyForecast.isWeatherWithPrecipitationsOrOvercast()
                    -> {
                    WeatherAndDayTimeState.NO_PRECIPITATION_NIGHT
                }

                hourlyForecast.isDay && hourlyForecast.isWeatherWithPrecipitationsOrOvercast()
                    -> {
                    WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_DAY
                }

                !hourlyForecast.isDay && hourlyForecast.isWeatherWithPrecipitationsOrOvercast()
                    -> {
                    WeatherAndDayTimeState.OVERCAST_OR_PRECIPITATION_NIGHT
                }

                else -> WeatherAndDayTimeState.NO_PRECIPITATION_DAY
            }
        } catch (e: IllegalStateException) {
            return WeatherAndDayTimeState.NO_PRECIPITATION_DAY
        }
    }

}