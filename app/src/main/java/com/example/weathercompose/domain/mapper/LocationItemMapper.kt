package com.example.weathercompose.domain.mapper

import android.content.Context
import com.example.weathercompose.R
import com.example.weathercompose.domain.model.forecast.WeatherDescription.Companion.weatherDescriptionToIconRes
import com.example.weathercompose.domain.model.forecast.WeatherDescription.Companion.weatherDescriptionToString
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.ui.model.LocationItem

class LocationItemMapper(private val context: Context) {
    fun mapToLocationItem(locationDomainModel: LocationDomainModel): LocationItem {
        val currentHourlyForecast = locationDomainModel.getForecastForCurrentHour()

        val (temperature, weatherDescription, icon) = if (currentHourlyForecast != null) {
            with(currentHourlyForecast) {
                Triple<String, Int, Int>(
                    context.getString(R.string.temperature_label, Math.round(temperature).toInt()),
                    weatherDescriptionToString(weatherDescription),
                    weatherDescriptionToIconRes(weatherDescription),
                )
            }
        } else {
            Triple<String, Int, Int>(
                "--",
                R.string.no_data,
                R.drawable.ic_launcher_background
            )
        }

        return LocationItem(
            id = locationDomainModel.id,
            name = locationDomainModel.name,
            currentHourTemperature = temperature,
            currentHourWeatherDescription = weatherDescription,
            currentHourWeatherIconRes = icon,
        )
    }
}