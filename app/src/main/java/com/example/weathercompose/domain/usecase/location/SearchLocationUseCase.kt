package com.example.weathercompose.domain.usecase.location

import com.example.weathercompose.data.api.GeocodingAPI
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.repository.LocationRepository
import retrofit2.HttpException
import java.io.IOException

class SearchLocationUseCase(private val locationRepository: LocationRepository) {

    suspend operator fun invoke(
        name: String,
        count: Int = GeocodingAPI.DEFAULT_NUMBER_OF_RESULTS,
        language: String = GeocodingAPI.DEFAULT_LANGUAGE,
        format: String = GeocodingAPI.DEFAULT_FORMAT,
    ): ResponseResult<List<LocationDomainModel>> {
        return try {
            val response = locationRepository.search(
                name = name,
                count = count,
                language = language,
                format = format,
            )
            ResponseResult.Success(data = response)
        } catch (error: HttpException) {
            ResponseResult.Error(code = error.code(), message = error.message())
        } catch (exception: IOException) {
            ResponseResult.Exception(throwable = exception)
        }
    }
}