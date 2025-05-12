package com.example.weathercompose.domain.usecase.settings

import com.example.weathercompose.data.datastore.AppSettings
import com.example.weathercompose.data.model.ForecastUpdateFrequency
import kotlinx.coroutines.flow.Flow

class GetForecastUpdateFrequencyUseCase(
    private val appSettings: AppSettings
) {

    operator fun invoke(): Flow<ForecastUpdateFrequency> {
        return appSettings.forecastUpdateFrequency
    }
}