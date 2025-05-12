package com.example.weathercompose.domain.usecase.settings

import com.example.weathercompose.data.datastore.AppSettings
import com.example.weathercompose.data.model.ForecastUpdateFrequency

class SetForecastUpdateFrequencyUseCase(
    private val appSettings: AppSettings
) {

    suspend operator fun invoke(forecastUpdateFrequency: ForecastUpdateFrequency) {
        appSettings.setForecastUpdateFrequency(forecastUpdateFrequency = forecastUpdateFrequency)
    }
}