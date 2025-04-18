package com.example.weathercompose.domain.usecase.location

import com.example.weathercompose.domain.repository.LocationRepository

class DeleteLocationUseCase(
    private val locationRepository: LocationRepository,
) {

    suspend operator fun invoke(locationId: Long) {
        locationRepository.deleteLocationById(locationId = locationId)
    }
}