package com.example.weathercompose.domain.mapper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.weathercompose.R
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.domain.model.forecast.WeatherDescription.Companion.weatherDescriptionToIconRes
import com.example.weathercompose.domain.model.forecast.WeatherDescription.Companion.weatherDescriptionToString
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.ui.model.LocationItem

class LocationItemMapper {
    fun mapToLocationItem(
        location: LocationDomainModel,
        temperatureUnit: TemperatureUnit,
    ): LocationItem {
        var temperature: String

        @StringRes
        var weatherDescription: Int

        @DrawableRes
        var icon: Int

        try {
            val currentHourlyForecast = location.getForecastForCurrentHour()

            temperature = TemperatureUnit.getTemperatureForUI(
                temperature = currentHourlyForecast.temperature,
                temperatureUnit = temperatureUnit,
            )

            weatherDescription =
                weatherDescriptionToString(currentHourlyForecast.weatherDescription)
            icon = weatherDescriptionToIconRes(currentHourlyForecast.weatherDescription)

        } catch (e: IllegalStateException) {
            temperature = "--"
            weatherDescription = R.string.no_data
            icon = R.drawable.ic_launcher_background
        }

        return LocationItem(
            id = location.id,
            name = location.name,
            currentHourTemperature = temperature,
            currentHourWeatherDescription = weatherDescription,
            currentHourWeatherIconRes = icon,
        )
    }
}