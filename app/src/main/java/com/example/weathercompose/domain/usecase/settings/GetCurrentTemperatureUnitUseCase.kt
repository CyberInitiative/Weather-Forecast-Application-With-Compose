package com.example.weathercompose.domain.usecase.settings

import com.example.weathercompose.data.datastore.AppSettings
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import kotlinx.coroutines.flow.Flow

class GetCurrentTemperatureUnitUseCase(
    private val appSettings: AppSettings
) {

    operator fun invoke(): Flow<TemperatureUnit> {
        return appSettings.currentTemperatureUnit
    }
}