package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.mapper.ForecastUIStateMapper
import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.usecase.forecast.DeleteForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveForecastUseCase
import com.example.weathercompose.domain.usecase.location.DeleteLocationUseCase
import com.example.weathercompose.domain.usecase.location.LoadAllLocationsUseCase
import com.example.weathercompose.domain.usecase.location.SaveLocationUseCase
import com.example.weathercompose.domain.usecase.location.SearchLocationUseCase
import com.example.weathercompose.ui.model.LocationItem
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.LocationForecastUIState
import com.example.weathercompose.ui.ui_state.LocationSearchUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val loadAllLocationsUseCase: LoadAllLocationsUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val forecastUIStateMapper: ForecastUIStateMapper,
    private val locationItemsMapper: LocationItemMapper,
    private val searchLocationUseCase: SearchLocationUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val saveForecastUseCase: SaveForecastUseCase,
    private val deleteForecastUseCase: DeleteForecastUseCase,
) : ViewModel() {
    private val _currentLocation = MutableStateFlow<LocationDomainModel?>(null)
    private val _loadedLocations = MutableStateFlow<List<LocationDomainModel>>(value = emptyList())

    private val _locationForecastUIState =
        MutableStateFlow<LocationForecastUIState>(LocationForecastUIState.InitialUIState)
    val locationForecastUIState: StateFlow<LocationForecastUIState> = _locationForecastUIState

    private val _locationItems = MutableStateFlow<List<LocationItem>>(emptyList())
    val locationItems: StateFlow<List<LocationItem>> = _locationItems.asStateFlow()

    private val _areLocationsEmpty = MutableStateFlow<Boolean?>(null)
    val areLocationsEmpty: StateFlow<Boolean?> = _areLocationsEmpty.asStateFlow()

    private val _locationSearchUIState = MutableStateFlow(LocationSearchUIState())
    val locationSearchUIState: StateFlow<LocationSearchUIState> = _locationSearchUIState

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
        val loadedLocations = loadAllLocationsUseCase()
        _loadedLocations.value = loadedLocations
        if (loadedLocations.isEmpty()) {
            _areLocationsEmpty.value = true
        }
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

                _locationItems.update {
                    locations.map { locationItemsMapper.mapToLocationItem(it) }
                }
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

                    if (updatedLocations.isEmpty()) {
                        _areLocationsEmpty.value = true
                    }

                    _loadedLocations.update { updatedLocations }
                    if (_currentLocation.value?.id == locationId && updatedLocations.isNotEmpty()) {
                        _currentLocation.update { updatedLocations[0] }
                    }
                }
        }
    }

    fun setCurrentLocationForecast(locationId: Long) {
        val currentLocations = _loadedLocations.value

        val loadedLocation = currentLocations.firstOrNull { it.id == locationId }
        _currentLocation.update { loadedLocation }
    }

    fun setLocationSearchUIStateLoading() {
        _locationSearchUIState.value = _locationSearchUIState.value.copy(isLoading = true)
    }

    suspend fun saveLocation(location: LocationDomainModel) {
        saveLocationUseCase.invoke(location = location)

    }

    fun addLocation(location: LocationDomainModel){
        _loadedLocations.update { _loadedLocations.value + location }
    }

    fun searchLocation(
        name: String,
    ) {
        viewModelScope.launch {
            _locationSearchUIState.value = _locationSearchUIState.value.copy(isLoading = true)
            val locationSearchResult = searchLocationUseCase.invoke(
                name = name,
            )

            when (locationSearchResult) {
                is ResponseResult.Success -> {
                    _locationSearchUIState.value = _locationSearchUIState.value.copy(
                        isLoading = false,
                        locations = locationSearchResult.data
                    )
                }

                is ResponseResult.Error -> {
                    val code = locationSearchResult.code
                    val message = locationSearchResult.message.orEmpty()
                    val errorMessage = "Request error; Code: $code; Message: $message"
                    _locationSearchUIState.value = _locationSearchUIState.value.copy(
                        isLoading = false,
                        locations = emptyList(),
                        errorMessage = errorMessage,
                    )
                }

                is ResponseResult.Exception -> {
                    _locationSearchUIState.value = _locationSearchUIState.value.copy(
                        isLoading = false,
                        locations = emptyList(),
                        errorMessage = locationSearchResult.throwable.message.orEmpty(),
                    )
                }
            }
        }
    }

    suspend fun loadForecastForLocation(
        location: LocationDomainModel
    ): LocationDomainModel {
        with(location) {
            val forecastLoadingResponseResult = loadForecastUseCase.invoke(
                latitude = latitude,
                longitude = longitude,
                timeZone = timeZone,
            )

            return when (forecastLoadingResponseResult) {
                is ResponseResult.Success -> {
                    val dailyForecasts = forecastLoadingResponseResult.data

                    deleteForecastUseCase.invoke(locationId = location.id)
                    saveForecastUseCase(
                        locationId = location.id,
                        dailyForecast = dailyForecasts,
                    )

                    location.copy(forecasts = dailyForecasts)
                }

                is ResponseResult.Error -> {
                    Log.d(
                        TAG, "loadForecastForCity() called; ResponseResult.Error: ${
                            forecastLoadingResponseResult.buildErrorMessage()
                        }"
                    )
                    location.copy(errorMessage = forecastLoadingResponseResult.buildErrorMessage())
                }

                is ResponseResult.Exception -> {
                    Log.d(
                        TAG, "loadForecastForCity() called; ResponseResult.Exception: ${
                            forecastLoadingResponseResult.buildExceptionMessage()
                        }"
                    )
                    location.copy(errorMessage = forecastLoadingResponseResult.buildExceptionMessage())
                }
            }
        }
    }

    companion object {
        private const val TAG = "ForecastViewModel"
    }
}