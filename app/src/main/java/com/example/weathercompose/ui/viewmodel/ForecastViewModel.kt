package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.Result
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.data.mapper.mapToLocationDomainModel
import com.example.weathercompose.domain.mapper.ForecastUIStateMapper
import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.domain.model.DataState
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.location.DeleteLocationUseCase
import com.example.weathercompose.domain.usecase.location.LoadAllLocationsUseCase
import com.example.weathercompose.domain.usecase.location.LoadLocationUseCase
import com.example.weathercompose.domain.usecase.location.SaveLocationUseCase
import com.example.weathercompose.domain.usecase.location.SearchLocationUseCase
import com.example.weathercompose.ui.model.LocationItem
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.LocationForecastState
import com.example.weathercompose.ui.ui_state.LocationSearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
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
    private val loadLocationUseCase: LoadLocationUseCase,
) : ViewModel() {
    private val _currentLocationId = MutableStateFlow<LocationDomainModel?>(value = null)
    private val _locationsState = MutableStateFlow<List<LocationDomainModel>?>(value = null)

    private val _locationForecastState =
        MutableStateFlow<LocationForecastState>(LocationForecastState.Loading)
    val locationForecastState: StateFlow<LocationForecastState> = _locationForecastState

    private val _locationItems = MutableStateFlow<List<LocationItem>>(emptyList())
    val locationItems: StateFlow<List<LocationItem>> = _locationItems.asStateFlow()

    private val _locationSearchState = MutableStateFlow(LocationSearchState())
    val locationSearchState: StateFlow<LocationSearchState> = _locationSearchState

    private val _precipitationCondition = MutableStateFlow(
        value = PrecipitationCondition.NO_PRECIPITATION_DAY
    )
    val precipitationCondition: StateFlow<PrecipitationCondition>
        get() = _precipitationCondition

    init {
        viewModelScope.launch {
            _locationsState
                .filterNotNull()
                .collect{ locations ->
                    _locationItems.update { locations.map { locationItemsMapper.mapToLocationItem(it) } }

                    if (locations.isNotEmpty() && _currentLocationId.value == null) {
                        setCurrentLocationForecast(locations.first().id)
                    }

                    locations.forEach { location ->
                        if (isNeedToLoadForecasts(location)) {
                            updateForecastForLocation(
                                locationId = location.id,
                                newForecastDataState = DataState.Loading,
                            )
                            Log.d(
                                TAG,
                                "Location: $location, currentState: ${location.forecastDataState}, " +
                                        "isTimestampExpired: ${location.isForecastLastUpdateTimestampExpired()}"
                            )
                            launch {
                                loadForecast(location = location)
                                Log.d(TAG, "Location: ${getLocation(location.id)}")
                            }
                        }
                    }
                }
        }
        observeCurrentLocation()
        loadLocations()
    }

    private fun isNeedToLoadForecasts(location: LocationDomainModel): Boolean {
        val isDataReady = location.forecastDataState is DataState.Ready
        val isDataLoading = location.forecastDataState is DataState.Loading
        val isTimestampExpired = location.isForecastLastUpdateTimestampExpired()
        return (!isDataReady || isTimestampExpired) && !isDataLoading
    }

    private suspend fun loadForecast(location: LocationDomainModel) {
        val forecastLoadResult = loadForecastUseCase(location)
        val forecastLoadState = when (forecastLoadResult.forecastLoadResult) {
            is Result.Success -> {
                DataState.Ready(forecastLoadResult.forecastLoadResult.data)
            }

            is Result.Error -> {
                DataState.Error(
                    IllegalStateException(
                        forecastLoadResult.forecastLoadResult.error
                    )
                )
            }
        }
        updateForecastForLocation(
            locationId = location.id,
            newForecastDataState = forecastLoadState,
            newForecastUpdateTimestamp = forecastLoadResult.forecastLoadTimestamp
        )
    }

    private fun loadLocations() {
        viewModelScope.launch {
            _locationsState.value = loadAllLocationsUseCase()
        }
    }

    private fun updateForecastForLocation(
        locationId: Long,
        newForecastDataState: DataState<List<DailyForecastDomainModel>>,
        newForecastUpdateTimestamp: Long = 0L,
    ) {
        _locationsState.update { currentList ->
            currentList?.map { location ->
                if (location.id == locationId) {
                    val timestamp = if (newForecastUpdateTimestamp == 0L) {
                        location.forecastLastUpdateTimestamp
                    } else {
                        newForecastUpdateTimestamp
                    }
                    val updatedLocation = location.copy(
                        forecastDataState = newForecastDataState,
                        forecastLastUpdateTimestamp = timestamp,
                    )
                    if (_currentLocationId.value?.id == updatedLocation.id) {
                        _currentLocationId.update { updatedLocation }
                    }
                    updatedLocation
                } else {
                    location
                }
            }
        }
    }

    private fun getLocation(
        locationId: Long,
    ): LocationDomainModel? {
        return _locationsState.value?.firstOrNull {
            it.id == locationId
        }
    }

    private fun observeCurrentLocation() {
        viewModelScope.launch {
            _currentLocationId.collect { location ->
                Log.d(TAG, "observeCurrentLocation triggered; location: $location")
                if (location != null) {
                    _precipitationCondition.value =
                        location.getPrecipitationsAndTimeOfDayStateForCurrentHour()

                    val locationForecastUIState =
                        forecastUIStateMapper.mapToUIState(location = location)
                    _locationForecastState.value = locationForecastUIState
                }
            }
        }
    }

    fun deleteLocation(locationId: Long) {
        viewModelScope.launch {
            deleteLocationUseCase(locationId = locationId)
            val locations = _locationsState.value?.toMutableList()
            if (!locations.isNullOrEmpty()) {
                locations.firstOrNull { it.id == locationId }?.let { deletedLocation ->
                    locations.remove(deletedLocation)
                    _locationsState.update { locations }

                    if (_currentLocationId.value?.id == deletedLocation.id) {
                        if (locations.isNotEmpty()) {
                            _currentLocationId.update { locations[0] }
                        } else {
                            _currentLocationId.update { null }
                        }
                    }
                }
            }
        }
    }

    fun setCurrentLocationForecast(locationId: Long) {
        viewModelScope.launch {
            val currentLocations = _locationsState.value
            if (!currentLocations.isNullOrEmpty()) {
                val currentLocation = currentLocations.firstOrNull { it.id == locationId }
                if (currentLocation != null) {
                    _currentLocationId.value = currentLocation
                }
            }
        }
    }

    fun searchLocation(
        name: String,
    ) {
        viewModelScope.launch {
            _locationSearchState.value = _locationSearchState.value.copy(isLoading = true)
            val locationSearchResult = searchLocationUseCase(name = name)

            when (locationSearchResult) {
                is Result.Success -> {
                    _locationSearchState.value = _locationSearchState.value.copy(
                        isLoading = false,
                        locations = locationSearchResult.data
                    )
                }

                is Result.Error -> {
                    _locationSearchState.value = _locationSearchState.value.copy(
                        isLoading = false,
                        locations = emptyList(),
                        errorMessage = locationSearchResult.error,
                    )
                }
            }
        }
    }

    fun clearLocationSearch() {
        _locationSearchState.value = _locationSearchState.value.copy(locations = emptyList())
    }

    suspend fun saveLocation(location: LocationEntity) {
        val existingLocation = loadLocationUseCase(locationId = location.locationId)
        if (existingLocation == null) {
            saveLocationUseCase(location = location)
            val mappedLocation = location.mapToLocationDomainModel()

            _locationsState.update { currentLocations ->
                currentLocations?.plus(mappedLocation) ?: listOf(mappedLocation)
            }
        }

        setCurrentLocationForecast(locationId = location.locationId)
    }

    companion object {
        private const val TAG = "ForecastViewModel"
    }
}

/*
private fun addLoadedLocation(location: LocationDomainModel) {
        val currentLocations = _locationsState.value
        if (currentLocations == null) {
            _locationsState.value = listOf(location)
        } else {
            if (currentLocations.firstOrNull { it.id == location.id } == null) {
                _locationsState.value = currentLocations + location
            }
        }
    }

private suspend fun saveForecastForLocation(
    location: LocationDomainModel
): LocationDomainModel {
    with(location) {
        val forecastLoadingResponseResult = loadForecastUseCase(
            latitude = latitude,
            longitude = longitude,
            timeZone = timeZone,
        )

        return when (forecastLoadingResponseResult) {
            is Result.Success -> {
                val dailyForecasts = forecastLoadingResponseResult.data

                deleteForecastUseCase(locationId = location.id)
                saveForecastUseCase(
                    locationId = location.id,
                    dailyForecastEntities = dailyForecasts,
                )

                location.copy(forecasts = dailyForecasts)
            }

            is Result.Error -> {
                location.copy(errorMessage = forecastLoadingResponseResult.error)
            }
        }
    }
}
 */