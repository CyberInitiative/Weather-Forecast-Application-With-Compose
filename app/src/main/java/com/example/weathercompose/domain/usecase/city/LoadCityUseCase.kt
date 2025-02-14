package com.example.weathercompose.domain.usecase.city

import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.repository.CityRepository

class LoadCityUseCase(private val cityRepository: CityRepository) {

    suspend operator fun invoke(cityId: Long): CityDomainModel{
        return cityRepository.load(cityId = cityId)
    }
}