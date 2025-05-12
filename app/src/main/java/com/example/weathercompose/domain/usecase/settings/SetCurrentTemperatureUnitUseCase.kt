package com.example.weathercompose.domain.usecase.settings

import com.example.weathercompose.data.datastore.AppSettings
import com.example.weathercompose.data.model.forecast.TemperatureUnit

class SetCurrentTemperatureUnitUseCase(
    private val appSettings: AppSettings
) {

    suspend operator fun invoke(temperatureUnit: TemperatureUnit) {
        appSettings.setCurrentTemperatureUnit(temperatureUnit = temperatureUnit)
    }
}