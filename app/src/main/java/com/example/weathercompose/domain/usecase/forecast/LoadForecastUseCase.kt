package com.example.weathercompose.domain.usecase.forecast

import com.example.weathercompose.data.api.ForecastService
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.mapper.DailyForecastMapper
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.repository.ForecastRepository
import retrofit2.HttpException
import java.io.IOException

class LoadForecastUseCase(
    private val forecastRepository: ForecastRepository,
    private val forecastMapper: DailyForecastMapper,
) {

    suspend fun execute(
        latitude: Double,
        longitude: Double,
        timeZone: String = ForecastService.DEFAULT_TIME_ZONE,
        dailyOptions: List<String> = ForecastService.dailyOptions,
        hourlyOptions: List<String> = ForecastService.hourlyOptions,
        forecastDays: Int = ForecastService.DEFAULT_FORECAST_DAYS,
    ): ResponseResult<List<DailyForecastDomainModel>> {
        return try {
            val response = forecastRepository.load(
                latitude = latitude,
                longitude = longitude,
                timeZone = timeZone,
                dailyOptions = dailyOptions,
                hourlyOptions = hourlyOptions,
                forecastDays = forecastDays,
            )
            val dailyForecasts = forecastMapper.mapToDomain(response)
            ResponseResult.Success(data = dailyForecasts)
        } catch (error: HttpException) {
            ResponseResult.Error(code = error.code(), message = error.message())
        } catch (exception: IOException) {
            ResponseResult.Exception(throwable = exception)
        }
    }

}