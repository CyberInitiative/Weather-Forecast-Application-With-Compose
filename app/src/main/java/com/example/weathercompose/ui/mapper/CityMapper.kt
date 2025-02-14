package com.example.weathercompose.ui.mapper

import android.content.Context
import com.example.weathercompose.R
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.model.forecast.WeatherDescription.Companion.weatherDescriptionToIconRes
import com.example.weathercompose.domain.model.forecast.WeatherDescription.Companion.weatherDescriptionToString
import com.example.weathercompose.ui.model.CityItem
import com.example.weathercompose.utils.getCurrentHourForTimeZone

class CityMapper(private val context: Context) {
    fun mapToCityItem(cityDomainModel: CityDomainModel): CityItem {
        val currentHour = getCurrentHourForTimeZone(cityDomainModel.timeZone)
        val currentHourlyForecast = cityDomainModel.forecast[0].hourlyForecasts.firstOrNull {
            it.hour == currentHour
        }

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

        return CityItem(
            id = cityDomainModel.id,
            name = cityDomainModel.name,
            currentHourTemperature = temperature,
            currentHourWeatherDescription = weatherDescription,
            currentHourWeatherIconRes = icon,
        )
    }
}