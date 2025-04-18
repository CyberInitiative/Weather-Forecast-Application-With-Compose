package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.domain.mapper.ForecastUIStateMapper
import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.usecase.location.DeleteLocationUseCase
import com.example.weathercompose.domain.usecase.location.LoadAllLocationsUseCase
import com.example.weathercompose.domain.usecase.location.LoadLocationUseCase
import com.example.weathercompose.ui.model.LocationItem
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.LocationForecastUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val loadLocationUseCase: LoadLocationUseCase,
    private val loadAllLocationsUseCase: LoadAllLocationsUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val forecastUIStateMapper: ForecastUIStateMapper,
    private val locationItemsMapper: LocationItemMapper,
) : ViewModel() {
    private val _currentLocation = MutableStateFlow<LocationDomainModel?>(null)

    private val _loadedLocations = MutableStateFlow<List<LocationDomainModel>>(value = emptyList())

    private val _locationForecastUIState =
        MutableStateFlow<LocationForecastUIState>(LocationForecastUIState.InitialUIState)
    val locationForecastUIState: StateFlow<LocationForecastUIState> = _locationForecastUIState

    private val _locationItems = MutableStateFlow<List<LocationItem>>(emptyList())
    val locationItems: StateFlow<List<LocationItem>> = _locationItems.asStateFlow()

    private val _isLocationsEmpty = MutableStateFlow<Boolean?>(null)
    val isLocationsEmpty: StateFlow<Boolean?> = _isLocationsEmpty.asStateFlow()

    private val _precipitationCondition = MutableStateFlow(
        value = PrecipitationCondition.NO_PRECIPITATION_DAY
    )
    val precipitationCondition: StateFlow<PrecipitationCondition>
        get() = _precipitationCondition

    init {
        observeLocations()
        observeCurrentLocation()

        viewModelScope.launch {
            loadLocations()
        }
    }

    private suspend fun loadLocations() {
        _loadedLocations.update { loadAllLocationsUseCase() }
    }

    private fun observeLocations() {
        viewModelScope.launch {
            _loadedLocations.collect { locations ->
                Log.d(TAG, "locations: \n${locations.joinToString("\n")}")
                val currentLocationValue = _currentLocation.value
                if (currentLocationValue != null) {
                    val updatedLocation = locations.find { it.id == currentLocationValue.id }
                    if (updatedLocation != null && updatedLocation != currentLocationValue) {
                        _currentLocation.update { updatedLocation }
                    }
                } else if (_currentLocation.value == null && locations.isNotEmpty()) {
                    _currentLocation.update { locations[0] }
                }

                _locationItems.update { locations.map { locationItemsMapper.mapToLocationItem(it) } }
            }
        }
    }

    private fun observeCurrentLocation() {
        viewModelScope.launch {
            _currentLocation.collect { location ->
                if (location != null) {
                    _precipitationCondition.value =
                        location.getPrecipitationsAndTimeOfDayStateForCurrentHour()
                    val locationForecastUIState =
                        forecastUIStateMapper.mapToUIState(location = location)
                    _locationForecastUIState.update {
                        (locationForecastUIState as LocationForecastUIState.LocationDataUIState)
                            .copy(
                                isDataLoading = false
                            )
                    }
                }
            }
        }
    }

    fun deleteLocation(locationId: Long) {
        viewModelScope.launch {
            deleteLocationUseCase.invoke(locationId = locationId)

            val updatedLocations = _loadedLocations.value.toMutableList()
            updatedLocations
                .firstOrNull { it.id == locationId }
                ?.let {
                    updatedLocations.remove(it)
                    _loadedLocations.update { updatedLocations }
                    if (_currentLocation.value?.id == locationId) {
                        _currentLocation.update { updatedLocations[0] }
                    }
                }
        }
    }

    suspend fun setCurrentLocationForecast(locationId: Long) {
        val currentLocations = _loadedLocations.value

        val loadedLocation = currentLocations.firstOrNull { location ->
            location.id == locationId
        } ?: loadLocationUseCase.invoke(locationId = locationId)

        if (loadedLocation == null) {
            return
        } else {
            _currentLocation.update { loadedLocation }
            if (currentLocations.firstOrNull { it.id == locationId } == null) {
                _loadedLocations.update { currentLocations + loadedLocation }
            }
        }
    }

    companion object {
        private const val TAG = "ForecastViewModel"
    }
}