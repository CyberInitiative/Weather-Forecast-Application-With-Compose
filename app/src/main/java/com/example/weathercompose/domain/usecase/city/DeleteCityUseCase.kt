package com.example.weathercompose.domain.usecase.city

import com.example.weathercompose.data.CityCacheManager
import com.example.weathercompose.domain.repository.CityRepository

class DeleteCityUseCase(
    private val cityRepository: CityRepository,
    private val cityCacheManager: CityCacheManager,
) {

    suspend operator fun invoke(cityId: Long) {
        cityCacheManager.deleteCityFromCache(cityId = cityId)
        cityRepository.deleteCityById(cityId = cityId)
    }
}