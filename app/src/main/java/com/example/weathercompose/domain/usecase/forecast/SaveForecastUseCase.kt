package com.example.weathercompose.domain.usecase.forecast

import android.util.Log
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.domain.repository.ForecastRepository
import com.example.weathercompose.domain.repository.LocationRepository

class SaveForecastUseCase(
    private val forecastRepository: ForecastRepository,
    private val locationRepository: LocationRepository,
) {

    suspend operator fun invoke(
        locationId: Long,
        dailyForecastEntities: List<DailyForecastEntity>
    ): Long {
        forecastRepository.saveForecastsForLocation(
            locationId = locationId,
            dailyForecastEntities = dailyForecastEntities,
        )

        val newTimestamp = System.currentTimeMillis()
        Log.d(
            TAG,
            "Called before updateForecastLastUpdateTimestamp() with timestamp: $newTimestamp"
        )
        locationRepository.updateForecastLastUpdateTimestamp(
            locationId = locationId,
            timestamp = newTimestamp
        )

        return newTimestamp
    }

    companion object {
        private const val TAG = "SaveForecastUseCase"
    }
}