package com.example.weathercompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.Result
import com.example.weathercompose.data.model.ForecastUpdateFrequency
import com.example.weathercompose.data.model.forecast.TemperatureUnit
import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.domain.mapper.LocationUIStateMapper
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.model.forecast.DataState
import com.example.weathercompose.domain.model.location.LocationDomainModel
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.location.DeleteLocationUseCase
import com.example.weathercompose.domain.usecase.location.LoadAllLocationsUseCase
import com.example.weathercompose.domain.usecase.location.LoadLocationUseCase
import com.example.weathercompose.domain.usecase.location.SaveLocationUseCase
import com.example.weathercompose.domain.usecase.location.SetLocationAsHomeUseCase
import com.example.weathercompose.domain.usecase.settings.GetCurrentTemperatureUnitUseCase
import com.example.weathercompose.domain.usecase.settings.GetForecastUpdateFrequencyUseCase
import com.example.weathercompose.domain.usecase.settings.SetCurrentTemperatureUnitUseCase
import com.example.weathercompose.domain.usecase.settings.SetForecastUpdateFrequencyUseCase
import com.example.weathercompose.ui.model.LocationItem
import com.example.weathercompose.ui.model.WeatherAndDayTimeState
import com.example.weathercompose.ui.ui_state.LocationUIState
import com.example.weathercompose.utils.NetworkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val loadAllLocationsUseCase: LoadAllLocationsUseCase,
    // private val loadLocationsUseCase: LoadLocationsUseCase,
    private val setLocationAsHomeUseCase: SetLocationAsHomeUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val locationUIStateMapper: LocationUIStateMapper,
    private val locationItemsMapper: LocationItemMapper,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val loadLocationUseCase: LoadLocationUseCase,
    private val setCurrentTemperatureUnitUseCase: SetCurrentTemperatureUnitUseCase,
    private val getTemperatureUnitUseCase: GetCurrentTemperatureUnitUseCase,
    private val setForecastUpdateFrequencyUseCase: SetForecastUpdateFrequencyUseCase,
    private val getForecastUpdateFrequencyUseCase: GetForecastUpdateFrequencyUseCase,
    private val networkManager: NetworkManager,
) : ViewModel() {
    private val _locationsState = MutableStateFlow<List<LocationDomainModel>?>(value = null)

    private val _locationsUIStates = MutableStateFlow<DataState<List<LocationUIState>>>(
        value = DataState.Initial
    )
    val locationsUIStates: StateFlow<DataState<List<LocationUIState>>>
        get() = _locationsUIStates.asStateFlow()

    private val _locationItems = MutableStateFlow<List<LocationItem>>(emptyList())
    val locationItems: StateFlow<List<LocationItem>> = _locationItems.asStateFlow()

    private var _currentTemperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
    val currentTemperatureUnit: TemperatureUnit get() = _currentTemperatureUnit

    private var _currentForecastUpdateFrequencyInHours: ForecastUpdateFrequency =
        ForecastUpdateFrequency.ONE_HOUR
    val currentForecastUpdateFrequencyInHours: ForecastUpdateFrequency
        get() = _currentForecastUpdateFrequencyInHours

    private val _weatherAndDayTimeState = MutableStateFlow(
        value = WeatherAndDayTimeState.NO_PRECIPITATION_DAY
    )
    val weatherAndDayTimeState: StateFlow<WeatherAndDayTimeState>
        get() = _weatherAndDayTimeState

    init {
        _locationsUIStates.value = DataState.Loading
        observeLocationsState()
        observeLocationsAndTemperatureUnit()
        observeTemperatureUnit()
        observeForecastUpdateFrequency()
//        loadAllLocations()
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            _locationsState.value = loadAllLocationsUseCase()
        }
    }

    /*
    private fun loadAllLocations() {
        viewModelScope.launch {
            loadLocationsUseCase().collect { locations ->
                _locationsState.value = locations
            }
        }
    }
     */

    private fun observeLocationsState() {
        viewModelScope.launch {
            _locationsState
                .filterNotNull()
                .collect { locations ->
                    val sortedLocations = locations.sortedByDescending { it.isHomeLocation }

                    sortedLocations.forEach { location ->
                        if (shouldLoadForecasts(location = location)) {
                            updateForecastDataStateForLocation(
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
    }

    private fun shouldLoadForecasts(location: LocationDomainModel): Boolean {
        val isDataReady = location.forecastDataState is DataState.Ready
        val isDataLoading = location.forecastDataState is DataState.Loading
        return !isDataReady && !isDataLoading
    }

    private fun observeLocationsAndTemperatureUnit() {
        viewModelScope.launch {
            combine(
                _locationsState.filterNotNull(),
                getTemperatureUnitUseCase()
            ) { locations, unit ->
                val sortedLocations = locations.sortedByDescending { it.isHomeLocation }

                updateLocationItems(locations = locations, temperatureUnit = unit)
                sortedLocations.map { location ->
                    locationUIStateMapper.mapToUIState(location = location, temperatureUnit = unit)
                }
            }.collect { items ->
                if (items.isNotEmpty()) {
                    _locationsUIStates.value = DataState.Ready(data = items)
                } else {
                    _locationsUIStates.value = DataState.NoData
                }
            }
        }
    }

    private fun observeTemperatureUnit() {
        viewModelScope.launch {
            getTemperatureUnitUseCase().collect { unit ->
                _currentTemperatureUnit = unit
            }
        }
    }

    private fun observeForecastUpdateFrequency() {
        viewModelScope.launch {
            getForecastUpdateFrequencyUseCase().collect { frequency ->
                _currentForecastUpdateFrequencyInHours = frequency
            }
        }
    }

    private suspend fun loadForecast(location: LocationDomainModel) {
        val forecastLoadResult = loadForecastUseCase(
            forceLoadFromNetwork = false,
            locationDomainModel = location
        )
        val forecastLoadState = when (forecastLoadResult) {
            is Result.Success -> {
                DataState.Ready(forecastLoadResult.data)
            }

            is Result.Error -> {
                DataState.Error(forecastLoadResult.error)
            }
        }
        updateForecastDataStateForLocation(
            locationId = location.id,
            newForecastDataState = forecastLoadState,
        )
    }

    private fun updateForecastDataStateForLocation(
        locationId: Long,
        newForecastDataState: DataState<List<DailyForecastDomainModel>>,
    ) {
        _locationsState.update { currentList ->
            currentList?.map { location ->
                if (location.id == locationId) {
                    val updatedLocation = location.copy(
                        forecastDataState = newForecastDataState,
                    )

                    updatedLocation
                } else {
                    location
                }
            }
        }
    }

    fun setLocationHomeStatus(locationId: Long) {
        viewModelScope.launch {
            setLocationAsHomeUseCase(locationId = locationId)

            _locationsState.update { currentList ->
                currentList?.map { location ->
                    if (location.id == locationId) {
                        if (location.isHomeLocation) {
                            location.copy(isHomeLocation = false)
                        } else {
                            location.copy(isHomeLocation = true)
                        }
                    } else {
                        if (location.isHomeLocation) {
                            location.copy(isHomeLocation = false)
                        } else {
                            location
                        }
                    }
                }
            }
        }
    }

    suspend fun setCurrentTemperatureUnit(temperatureUnit: TemperatureUnit) {
        setCurrentTemperatureUnitUseCase(temperatureUnit = temperatureUnit)
    }

    suspend fun setForecastUpdateFrequency(forecastUpdateFrequency: ForecastUpdateFrequency) {
        setForecastUpdateFrequencyUseCase(forecastUpdateFrequency = forecastUpdateFrequency)
    }

    private fun updateLocationItems(
        locations: List<LocationDomainModel>,
        temperatureUnit: TemperatureUnit,
    ) {
        _locationItems.update {
            locations.map { location ->
                locationItemsMapper.mapToLocationItem(
                    location = location,
                    temperatureUnit = temperatureUnit,
                )
            }
        }
    }

    /*
    private fun updateLocationsUIStates(locations: List<LocationDomainModel>) {
        _locationsUIStates.update { locations.map { locationUIStateMapper.mapToUIState(it) } }
    }
     */

    fun isNetworkAvailable(): Boolean {
        return networkManager.isInternetAvailable()
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
        (_locationsUIStates.value as? DataState.Ready)?.let { state ->
            _weatherAndDayTimeState.value = state.data[page].weatherAndDayTimeState
        }
    }

    suspend fun loadLocationIfAbsent(locationId: Long) {
        val searchedLocation = _locationsState.value?.firstOrNull { location ->
            location.id == locationId
        }

        if(searchedLocation == null){
            _locationsUIStates.value = DataState.Loading
            loadLocationUseCase(locationId = locationId)?.let { loadedLocation ->
                _locationsState.update { locations ->
                    locations?.plus(loadedLocation) ?: listOf(loadedLocation)
                }
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