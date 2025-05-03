package com.example.weathercompose.domain.model

import com.example.weathercompose.data.api.Result
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel

data class ForecastLoadResult(
    val forecastLoadTimestamp: Long = 0L,
    val forecastLoadResult: Result<List<DailyForecastDomainModel>>
)