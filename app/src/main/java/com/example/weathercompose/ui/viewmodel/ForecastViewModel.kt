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
    private val _currentLocation = MutableStateFlow<LocationDomainModel?>(value = null)
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
        Log.d(TAG, "INIT CALLED")
        viewModelScope.launch {
            _locationsState
                .filterNotNull()
                .collect { locations ->
                    updateLocationItems(locations = locations)
                    onLocationDeleted(locations = locations)

                    if (locations.isNotEmpty() && _currentLocation.value == null) {
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
                                Log.d(TAG, "Location: $location}")
                            }
                        }
                    }
                }
        }
        observeCurrentLocation()
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            _locationsState.value = loadAllLocationsUseCase()
        }
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
                    if (_currentLocation.value?.id == updatedLocation.id) {
                        _currentLocation.update { updatedLocation }
                    }
                    updatedLocation
                } else {
                    location
                }
            }
        }
    }

    private fun observeCurrentLocation() {
        viewModelScope.launch {
            _currentLocation.collect { location ->
                Log.d(TAG, "observeCurrentLocation triggered; location: $location")
                if (location != null) {
                    _precipitationCondition.value =
                        location.getPrecipitationsAndTimeOfDayStateForCurrentHour()

                    val locationForecastUIState =
                        forecastUIStateMapper.mapToUIState(location = location)
                    _locationForecastState.value = locationForecastUIState
                } else {
                    if (_locationsState.value?.isEmpty() == true){
                        _locationForecastState.value = LocationForecastState.NoLocationData
                    } else {
                        _locationForecastState.value = LocationForecastState.Loading
                    }
                }
            }
        }
    }

    fun deleteLocation(locationId: Long) {
        viewModelScope.launch {
            deleteLocationUseCase(locationId = locationId)
            _locationsState.update { locations ->
                locations?.filterNot { it.id == locationId }
            }
        }
    }

    private fun onLocationDeleted(locations: List<LocationDomainModel>) {
        Log.d(TAG, "onLocationDeleted() called;")
        val currentLocation = _currentLocation.value
        if (locations.isEmpty()) {
            Log.d(TAG, "locations.isEmpty();")
            _locationForecastState.update { LocationForecastState.NoLocationData }
            _currentLocation.update { null }
        }
        else if (currentLocation != null && locations.none { it.id == currentLocation.id }) {
            Log.d(TAG, "else")
            _currentLocation.update { locations.firstOrNull() }
        }
    }

    private fun updateLocationItems(locations: List<LocationDomainModel>) {
        _locationItems.update { locations.map { locationItemsMapper.mapToLocationItem(it) } }
    }

    fun setCurrentLocationForecast(locationId: Long) {
        viewModelScope.launch {
            val currentLocations = _locationsState.value
            if (!currentLocations.isNullOrEmpty()) {
                val currentLocation = currentLocations.firstOrNull { it.id == locationId }
                if (currentLocation != null) {
                    _currentLocation.value = currentLocation
                }
            }
        }
    }

    fun searchLocation(
        name: String,
    ) {
        viewModelScope.launch {
            _locationSearchState.value = _locationSearchState.value.copy(isLoading = true)
            when (val locationSearchResult = searchLocationUseCase(name = name)) {
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

    suspend fun saveLocation(locationEntity: LocationEntity) {
        val existingLocation = loadLocationUseCase(locationId = locationEntity.locationId)

        if (existingLocation == null) {
            saveLocationUseCase(location = locationEntity)
            val locationDomainModel = locationEntity.mapToLocationDomainModel()

            _locationsState.update { locations ->
                locations?.plus(locationDomainModel) ?: listOf(locationDomainModel)
            }
        }

        setCurrentLocationForecast(locationId = locationEntity.locationId)
    }

    companion object {
        private const val TAG = "ForecastViewModel"
    }
}