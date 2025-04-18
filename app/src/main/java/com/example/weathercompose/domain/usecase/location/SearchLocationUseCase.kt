package com.example.weathercompose.domain.usecase.location

import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.repository.LocationRepository
import retrofit2.HttpException
import java.io.IOException

class SearchLocationUseCase(private val locationRepository: LocationRepository) {

    suspend fun execute(
        name: String,
        count: Int,
        language: String,
        format: String,
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