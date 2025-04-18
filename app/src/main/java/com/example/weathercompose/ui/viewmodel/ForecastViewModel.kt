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
import com.example.weathercompose.domain.usecase.location.LoadAllCitiesUseCase
import com.example.weathercompose.domain.usecase.location.LoadLocationUseCase
import com.example.weathercompose.ui.model.LocationItem
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.LocationForecastUIState
import com.example.weathercompose.utils.NetworkManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val loadLocationUseCase: LoadLocationUseCase,
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val saveForecastUseCase: SaveForecastUseCase,
    private val deleteForecastUseCase: DeleteForecastUseCase,
    private val networkManager: NetworkManager,
    private val forecastUIStateMapper: ForecastUIStateMapper,
    private val locationItemsMapper: LocationItemMapper,
) : ViewModel() {
    private val _loadedLocations = MutableStateFlow<List<LocationDomainModel>>(value = emptyList())

    private val _currentLocation = MutableStateFlow<LocationDomainModel?>(null)

    private val _locationForecastUIState =
        MutableStateFlow<LocationForecastUIState>(LocationForecastUIState.InitialUIState)
    val locationForecastUIState: StateFlow<LocationForecastUIState> = _locationForecastUIState

    private val _locationItems = MutableStateFlow<List<LocationItem>>(emptyList())
    val locationItems: StateFlow<List<LocationItem>> = _locationItems.asStateFlow()

    private val _isCitiesEmpty = MutableStateFlow<Boolean?>(null)
    val isCitiesEmpty: StateFlow<Boolean?> = _isCitiesEmpty.asStateFlow()

    private val _precipitationCondition = MutableStateFlow(
        value = PrecipitationCondition.NO_PRECIPITATION_DAY
    )
    val precipitationCondition: StateFlow<PrecipitationCondition>
        get() = _precipitationCondition

    init {
        Log.d(TAG, "init invoked")
        observeLocations()
        observeCurrentLocation()

        viewModelScope.launch {
            loadLocations()
            if (networkManager.isInternetAvailable()) {
                loadForecastsForLoadedCities()
            }
        }
    }

    private suspend fun loadLocations() {
        _loadedLocations.update { loadAllCitiesUseCase() }
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

    private suspend fun loadForecastsForLoadedCities() {
        val loadedLocationsValue = _loadedLocations.value

        coroutineScope {
            loadedLocationsValue.forEach { location ->
                launch {
                    val result = loadForecastForLocation(location)
                    _loadedLocations.update { currentList ->
                        currentList.map { existing ->
                            if (existing.id == result.id) result else existing
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadForecastForLocation(location: LocationDomainModel): LocationDomainModel {
        with(location) {
            val forecastLoadingResponseResult = loadForecastUseCase.execute(
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
                        TAG, "loadForecastForLocation() called; ResponseResult.Error: ${
                            forecastLoadingResponseResult.buildErrorMessage()
                        }"
                    )
                    location.copy(errorMessage = forecastLoadingResponseResult.buildErrorMessage())
                }

                is ResponseResult.Exception -> {
                    Log.d(
                        TAG, "loadForecastForLocation() called; ResponseResult.Exception: ${
                            forecastLoadingResponseResult.buildExceptionMessage()
                        }"
                    )
                    location.copy(errorMessage = forecastLoadingResponseResult.buildExceptionMessage())
                }
            }
        }
    }

    fun deleteLocation(locationId: Long) {
        viewModelScope.launch {
            deleteLocationUseCase.invoke(locationId = locationId)
            val loadedCitiesValue = _loadedLocations.value
            loadedCitiesValue.firstOrNull {
                it.id == locationId
            }?.let {
                val currentLocations = loadedCitiesValue.toMutableList()
                currentLocations.remove(it)
                _loadedLocations.update { currentLocations }
                if (_currentLocation.value?.id == it.id && loadedCitiesValue.isNotEmpty()) {
                    _currentLocation.value = loadedCitiesValue[0]
                }
            }

            _locationItems.value.firstOrNull {
                it.id == locationId
            }?.let {
                val newList = _locationItems.value.toMutableList()
                newList.remove(it)
                _locationItems.update { newList }
            }
        }
    }

    suspend fun setCurrentLocationForecast(locationId: Long) {
        _locationForecastUIState.update {
            LocationForecastUIState.LoadingUIState
        }

        val loadedLocationsValue = _loadedLocations.value

        val loadedLocation =
            loadedLocationsValue.firstOrNull { location -> location.id == locationId }
                ?: loadLocationUseCase.invoke(
                    locationId = locationId
                )

        val currentLocation = if (loadedLocation.forecasts.isEmpty()) {
            loadForecastForLocation(loadedLocation)
        } else {
            loadedLocation
        }

        _currentLocation.update { currentLocation }

        if (loadedLocationsValue.firstOrNull { it.id == currentLocation.id } == null) {
            val merged = loadedLocationsValue + currentLocation
            _loadedLocations.update { merged }
        }
        _precipitationCondition.value =
            currentLocation.getPrecipitationsAndTimeOfDayStateForCurrentHour()
        val locationForecastUIState = forecastUIStateMapper.mapToUIState(location = currentLocation)
        _locationForecastUIState.update {
            (locationForecastUIState as LocationForecastUIState.LocationDataUIState).copy(
                isDataLoading = false
            )
        }
    }

    companion object {
        private const val TAG = "ForecastViewModel"
    }
}
