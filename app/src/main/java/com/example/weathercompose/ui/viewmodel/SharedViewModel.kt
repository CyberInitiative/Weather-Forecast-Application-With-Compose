package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.ForecastService
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.ui.mapper.ForecastMapper
import com.example.weathercompose.ui.model.CityUIState
import com.example.weathercompose.ui.ui_state.LoadCitiesUIState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SharedViewModel(
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val forecastMapper: ForecastMapper
) : ViewModel() {
    private val _citiesState = MutableStateFlow<List<CityDomainModel>>(emptyList())

    private val _uiState = MutableStateFlow(LoadCitiesUIState())
    val uiState: StateFlow<LoadCitiesUIState> = _uiState.asStateFlow()

//    val citiesState: StateFlow<List<CityDomainModel>> = _citiesState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCitiesAndForecasts()
        }
    }

    private suspend fun loadCitiesAndForecasts() {
        val loadedCities = loadAllCitiesUseCase.execute()

        if (loadedCities.isEmpty()) {
            Log.d(TAG, "LoadedCities is empty")
            _uiState.update {
                it.copy(
                    cities = emptyList(),
                    isLoading = false,
                )
            }
            return
        }

        Log.d(TAG, "LoadedCities: ${loadedCities.joinToString()}")

        val loadedCitiesWithForecast = coroutineScope {
            loadedCities.map { city ->
                async {
                    val forecastLoadingResult = loadForecastUseCase.execute(
                        latitude = city.latitude,
                        longitude = city.longitude,
                        timeZone = city.timeZone,
                        dailyOptions = ForecastService.dailyOptions,
                        hourlyOptions = ForecastService.hourlyOptions,
                        forecastDays = ForecastService.DEFAULT_FORECAST_DAYS,
                    )

                    when (forecastLoadingResult) {
                        is ResponseResult.Success -> {
                            CityUIState(
                                id = city.id.toString(),
                                name = city.name,
                                country = city.country,
                                firstAdministrativeLevel = city.firstAdministrativeLevel,
                                secondAdministrativeLevel = city.secondAdministrativeLevel,
                                thirdAdministrativeLevel = city.thirdAdministrativeLevel,
                                fourthAdministrativeLevel = city.fourthAdministrativeLevel,
                                forecasts = forecastMapper.mapForecast(
                                    forecasts = forecastLoadingResult.data
                                )
                            )
                        }

                        is ResponseResult.Error -> {
                            CityUIState(
                                errorMessage = forecastLoadingResult.buildErrorMessage()
                            )
                        }

                        is ResponseResult.Exception -> {
                            CityUIState(
                                errorMessage = forecastLoadingResult.buildExceptionMessage()
                            )
                        }
                    }
                }
            }.awaitAll()
        }

        _uiState.update {
            it.copy(
                cities = loadedCitiesWithForecast,
                isLoading = false,
            )
        }
    }

    companion object {
        private const val TAG = "SharedViewModel"
    }
}