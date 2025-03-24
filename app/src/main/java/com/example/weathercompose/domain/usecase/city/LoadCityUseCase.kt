package com.example.weathercompose.domain.usecase.city

import com.example.weathercompose.data.CityCacheManager
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.repository.CityRepository

class LoadCityUseCase(
    private val cityRepository: CityRepository,
    private val cityCacheManager: CityCacheManager,
) {

    suspend operator fun invoke(cityId: Long): CityDomainModel {
        return cityCacheManager.getCityCache(cityId)
            ?: cityRepository.load(cityId).also {
                cityCacheManager.putCityCache(cityId, it)
            }
    }
}