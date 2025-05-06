package com.example.weathercompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.Result
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.data.mapper.mapToLocationDomainModel
import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.domain.mapper.LocationUIStateMapper
import com.example.weathercompose.domain.model.DataState
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.location.DeleteLocationUseCase
import com.example.weathercompose.domain.usecase.location.LoadAllLocationsUseCase
import com.example.weathercompose.domain.usecase.location.LoadLocationUseCase
import com.example.weathercompose.domain.usecase.location.SaveLocationUseCase
import com.example.weathercompose.ui.model.LocationItem
import com.example.weathercompose.ui.model.PrecipitationCondition
import com.example.weathercompose.ui.ui_state.LocationUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val loadAllLocationsUseCase: LoadAllLocationsUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val locationUIStateMapper: LocationUIStateMapper,
    private val locationItemsMapper: LocationItemMapper,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val loadLocationUseCase: LoadLocationUseCase,
) : ViewModel() {
    private val _locationsState = MutableStateFlow<List<LocationDomainModel>?>(value = null)
    val locationsState: StateFlow<List<LocationDomainModel>?> get() = _locationsState.asStateFlow()

    private val _locationsUIStates = MutableStateFlow<List<LocationUIState>?>(value = null)
    val locationsUIStates: StateFlow<List<LocationUIState>?>
        get() =
            _locationsUIStates.asStateFlow()

    private val _locationItems = MutableStateFlow<List<LocationItem>>(emptyList())
    val locationItems: StateFlow<List<LocationItem>> = _locationItems.asStateFlow()

    private val _precipitationCondition = MutableStateFlow(
        value = PrecipitationCondition.NO_PRECIPITATION_DAY
    )
    val precipitationCondition: StateFlow<PrecipitationCondition>
        get() = _precipitationCondition

    init {
        viewModelScope.launch {
            _locationsState
                .filterNotNull()
                .collect { locations ->
                    updateLocationItems(locations = locations)
                    updateLocationsUIStates(locations = locations)

                    locations.forEach { location ->
                        if (isNeedToLoadForecasts(location)) {
                            updateForecastForLocation(
                                locationId = location.id,
                                newForecastDataState = DataState.Loading,
                            )
                            launch {
                                loadForecast(location = location)
                            }
                        }
                    }
                }
        }

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
                DataState.Error(forecastLoadResult.forecastLoadResult.error)
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

                    updatedLocation
                } else {
                    location
                }
            }
        }
    }

    private fun updateLocationItems(locations: List<LocationDomainModel>) {
        _locationItems.update { locations.map { locationItemsMapper.mapToLocationItem(it) } }
    }

    private fun updateLocationsUIStates(locations: List<LocationDomainModel>) {
        _locationsUIStates.update { locations.map { locationUIStateMapper.mapToUIState(it) } }
    }

    fun deleteLocation(locationId: Long) {
        viewModelScope.launch {
            deleteLocationUseCase(locationId = locationId)
            _locationsState.update { locations ->
                locations?.filterNot { it.id == locationId }
            }
        }
    }

    fun onPageSelected(page: Int) {
        _locationsState.value?.get(page)?.let {
            _precipitationCondition.value = it.getPrecipitationsAndTimeOfDayStateForCurrentHour()
        }
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
    }

    fun isLocationsEmpty(): Boolean {
        return _locationsState.value?.isEmpty() ?: false
    }

    companion object {
        private const val TAG = "ForecastViewModel"
    }
}