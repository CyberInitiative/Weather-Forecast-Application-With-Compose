package com.example.weathercompose.domain.usecase.location

import com.example.weathercompose.data.api.GeocodingAPI
import com.example.weathercompose.data.api.Result
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.domain.repository.LocationRepository
import retrofit2.HttpException
import java.io.IOException

class SearchLocationUseCase(private val locationRepository: LocationRepository) {

    suspend operator fun invoke(
        name: String,
        count: Int = GeocodingAPI.DEFAULT_NUMBER_OF_RESULTS,
        language: String = GeocodingAPI.DEFAULT_LANGUAGE,
        format: String = GeocodingAPI.DEFAULT_FORMAT,
    ): Result<List<LocationEntity>> {
        return try {
            val response = locationRepository.search(
                name = name,
                count = count,
                language = language,
                format = format,
            )
            Result.Success(data = response)
        } catch (e: Exception){
            when (e) {
                is HttpException -> Result.Error(error = "Failed with code ${e.code()}, message: ${e.message()}")
                is IOException -> Result.Error(error = "Failed with IOException; message: ${e.message}")
                else -> Result.Error(error = "Failed with exception; message: ${e.message}")
            }
        }
    }
}