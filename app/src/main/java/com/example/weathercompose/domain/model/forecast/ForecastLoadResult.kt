package com.example.weathercompose.domain.model.forecast

import com.example.weathercompose.data.api.Result

data class ForecastLoadResult(
    val forecastLoadTimestamp: Long = 0L,
    val forecastLoadResult: Result<List<DailyForecastDomainModel>>
)