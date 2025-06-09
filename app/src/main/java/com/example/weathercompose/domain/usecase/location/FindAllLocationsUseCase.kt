package com.example.weathercompose.domain.usecase.location

import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.repository.LocationRepository

class FindAllLocationsUseCase(
    private val locationRepository: LocationRepository,
) {

    suspend operator fun invoke(): List<LocationDomainModel> = locationRepository.findAll()
}