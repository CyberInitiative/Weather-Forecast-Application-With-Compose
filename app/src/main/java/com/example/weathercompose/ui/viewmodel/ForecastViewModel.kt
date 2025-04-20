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
import com.example.weathercompose.ui.ui_state.LocationForecastState
import com.example.weathercompose.ui.ui_state.LocationSearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _currentLocation = MutableStateFlow<LocationDomainModel?>(value = null)
    private val _locationsState = MutableStateFlow<List<LocationDomainModel>?>(value = null)

    private val _locationForecastState =
        MutableStateFlow<LocationForecastState>(LocationForecastState.LoadingState)
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
        observeLocations()
        observeCurrentLocation()

        viewModelScope.launch {
            loadLocations()
        }
    }

    suspend fun saveLocation(location: LocationDomainModel) {
        _locationForecastState.value = LocationForecastState.LoadingState
        _locationsState.value?.let { currentLocations ->
            if (currentLocations.firstOrNull { it.id == location.id } == null) {
                saveLocationUseCase.invoke(location = location)
                val result = loadForecastForLocation(location)
                addLoadedLocation(location = result)
                _currentLocation.value = result
            } else {
                _currentLocation.value = location
            }
        }
    }

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

    private suspend fun loadLocations() {
        val loadedLocations = loadAllLocationsUseCase()
        _locationsState.value = loadedLocations
    }

    private fun observeLocations() {
        viewModelScope.launch {
            _locationsState.collect { state ->
                if (state != null) {

                    _locationItems.value =
                        state.map { locationItemsMapper.mapToLocationItem(it) }

                    when {
                        state.isNotEmpty() -> {
                            if (_currentLocation.value == null) {
                                _currentLocation.value = state[0]
                            }
                        }

                        else -> {
                            _locationForecastState.value =
                                LocationForecastState.NoLocationDataForecastState
                        }
                    }
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
                    _locationForecastState.value = locationForecastUIState
                }
            }
        }
    }

    fun deleteLocation(locationId: Long) {
        viewModelScope.launch {
            deleteLocationUseCase.invoke(locationId = locationId)
            val currentLocations = _locationsState.value
            if (currentLocations != null) {
                val deletedLocation = currentLocations.firstOrNull { it.id == locationId }
                if (deletedLocation != null) {
                    val updatedLocations = currentLocations.toMutableList()
                    updatedLocations.remove(deletedLocation)
                    _locationsState.value = updatedLocations

                    if (_currentLocation.value?.id == deletedLocation.id
                        && updatedLocations.isNotEmpty()
                    ) {
                        _currentLocation.value = updatedLocations[0]
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
            val locationSearchResult = searchLocationUseCase.invoke(
                name = name,
            )

            when (locationSearchResult) {
                is ResponseResult.Success -> {
                    _locationSearchState.value = _locationSearchState.value.copy(
                        isLoading = false,
                        locations = locationSearchResult.data
                    )
                }

                is ResponseResult.Error -> {
                    val code = locationSearchResult.code
                    val message = locationSearchResult.message.orEmpty()
                    val errorMessage = "Request error; Code: $code; Message: $message"
                    _locationSearchState.value = _locationSearchState.value.copy(
                        isLoading = false,
                        locations = emptyList(),
                        errorMessage = errorMessage,
                    )
                }

                is ResponseResult.Exception -> {
                    _locationSearchState.value = _locationSearchState.value.copy(
                        isLoading = false,
                        locations = emptyList(),
                        errorMessage = locationSearchResult.throwable.message.orEmpty(),
                    )
                }
            }
        }
    }

    private suspend fun loadForecastForLocation(
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