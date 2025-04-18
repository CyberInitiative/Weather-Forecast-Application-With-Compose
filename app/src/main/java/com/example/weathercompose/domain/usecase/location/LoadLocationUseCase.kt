package com.example.weathercompose.domain.usecase.location

import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.repository.LocationRepository

class LoadLocationUseCase(
    private val locationRepository: LocationRepository,
) {

    suspend operator fun invoke(locationId: Long): LocationDomainModel =
        locationRepository.load(locationId)
}