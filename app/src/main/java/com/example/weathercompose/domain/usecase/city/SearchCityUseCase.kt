package com.example.weathercompose.domain.usecase.city

import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.repository.CityRepository
import retrofit2.HttpException
import java.io.IOException

class SearchCityUseCase(private val cityRepository: CityRepository) {

    suspend fun execute(
        name: String,
        count: Int,
        language: String,
        format: String,
    ): ResponseResult<List<CityDomainModel>> {
        return try {
            val response = cityRepository.search(
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