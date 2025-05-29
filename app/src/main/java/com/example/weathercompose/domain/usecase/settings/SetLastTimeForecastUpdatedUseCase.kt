package com.example.weathercompose.domain.usecase.settings

import com.example.weathercompose.data.datastore.AppSettings

class SetLastTimeForecastUpdatedUseCase(
    private val appSettings: AppSettings
) {

    suspend operator fun invoke(time: Long) {
        appSettings.setLastTimeForecastsUpdated(lastTimeForecastsUpdated = time)
    }
}