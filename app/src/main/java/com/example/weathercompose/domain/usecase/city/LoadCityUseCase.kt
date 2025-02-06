package com.example.weathercompose.domain.usecase.city

import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.repository.CityRepository

class LoadCityUseCase(private val cityRepository: CityRepository) {

    suspend fun execute(cityId: Long): CityDomainModel{
        return cityRepository.load(cityId = cityId)
    }
}