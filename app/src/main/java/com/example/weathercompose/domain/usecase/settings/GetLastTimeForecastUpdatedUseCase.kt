package com.example.weathercompose.domain.usecase.settings

import com.example.weathercompose.data.datastore.AppSettings
import kotlinx.coroutines.flow.Flow

class GetLastTimeForecastUpdatedUseCase(
    private val appSettings: AppSettings
) {

    operator fun invoke(): Flow<Long> {
        return appSettings.lastTimeForecastsUpdated
    }
}