package com.example.weathercompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.api.ForecastService
import com.example.weathercompose.data.api.ResponseResult
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.domain.model.forecast.DailyForecastDomainModel
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.domain.usecase.city.LoadCityUseCase
import com.example.weathercompose.domain.usecase.city.SaveCityUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveDailyForecastsUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveHourlyForecastsUseCase
import com.example.weathercompose.ui.mapper.CityUIModelMapper
import com.example.weathercompose.ui.model.CityUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class MainViewModel(
    private val loadAllCitiesUseCase: LoadAllCitiesUseCase,
    private val loadCityUseCase: LoadCityUseCase,
    private val loadForecastUseCase: LoadForecastUseCase,
    private val cityUIModelMapper: CityUIModelMapper,
    private val saveDailyForecastsUseCase: SaveDailyForecastsUseCase,
    private val saveHourlyForecastsUseCase: SaveHourlyForecastsUseCase,
    private val saveCityUseCase: SaveCityUseCase,
) : ViewModel() {
    private val _loadedCities = MutableStateFlow<List<CityDomainModel>>(emptyList())

    private val _currentCityUIState = MutableStateFlow(CityUIState())
    val currentCityUIState: StateFlow<CityUIState> = _currentCityUIState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCities()
            loadForecasts()
            _loadedCities.collect {
                if (it.isNotEmpty()) {
                    setCurrentCity(it[0])
                }
            }
        }
    }

    private suspend fun loadCities() {
        val loadedCities = loadAllCitiesUseCase.execute().toMutableList()
        _loadedCities.update { loadedCities }
    }

    fun loadForecasts() {
        viewModelScope.launch {
            val loadedCities = _loadedCities.value.toMutableList()
            val jobs = mutableListOf<Job>()

            for (cityIndex in loadedCities.indices) {
                val city = loadedCities[cityIndex]

                if (city.forecast.isEmpty()) {
                    val forecastLoadingJob = launch {
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
                                val dailyForecasts = forecastLoadingResult.data

                                loadedCities[cityIndex] = city.copy(
                                    forecast = dailyForecasts
                                )

                                saveForecasts(dailyForecasts = dailyForecasts, cityId = city.id)
                            }

                            else -> {
                                loadedCities[cityIndex] = city.copy(
                                    forecast = emptyList()
                                )
                            }
                        }
                    }
                    jobs.add(forecastLoadingJob)
                }
            }
            jobs.joinAll()
            _loadedCities.update { loadedCities }
        }
    }

    fun saveCity(city: CityDomainModel): Job {
        return viewModelScope.launch {
            val id = saveCityUseCase.execute(city = city)
            Log.d(TAG, "saveCity() called; id: $id")
            setCurrentCity(city)
        }
    }

    suspend fun loadCity(cityId: Long): CityDomainModel {
        return loadCityUseCase.execute(cityId = cityId)
    }

    fun setCurrentCity(cityId: Long) {
        val city = _loadedCities.value.firstOrNull { it.id == cityId }
        if (city != null) {
            setCurrentCity(city)
        } else {
            viewModelScope.launch {
                setCurrentCity(loadCity(cityId = cityId))
            }
        }
    }

    fun setCurrentCity(city: CityDomainModel, id: Long = 0) {
        with(city) {
            if (forecast.isEmpty()) {
                viewModelScope.launch {
                    val response = loadForecastUseCase.execute(
                        latitude = latitude,
                        longitude = longitude,
                        timeZone = timeZone,
                    )

                    when (response) {
                        is ResponseResult.Success -> {
                            val dailyForecasts = response.data

                            val newCity = city.copy(forecast = dailyForecasts)
                            val newCityUIState = cityUIModelMapper.mapToUIModel(newCity)

                            saveForecasts(dailyForecasts = dailyForecasts, cityId = city.id)

                            _currentCityUIState.value = newCityUIState.copy(isLoading = false)
                        }

                        is ResponseResult.Error -> {
                            val newCityUIState = cityUIModelMapper.mapToUIModel(city)
                            _currentCityUIState.value = newCityUIState.copy(
                                forecastLoadingErrorMessage = response.buildErrorMessage(),
                                isLoading = false,
                            )
                        }

                        is ResponseResult.Exception -> {
                            val newCityUIState = cityUIModelMapper.mapToUIModel(city)
                            _currentCityUIState.value = newCityUIState.copy(
                                forecastLoadingErrorMessage = response.buildExceptionMessage(),
                                isLoading = false,
                            )
                        }
                    }
                }
            } else {
                val newCityUIState = cityUIModelMapper.mapToUIModel(city)
                _currentCityUIState.value = newCityUIState.copy(isLoading = false)
            }
        }
    }

    private suspend fun saveForecasts(
        dailyForecasts: List<DailyForecastDomainModel>,
        cityId: Long
    ) = coroutineScope {
        val saveDailyForecastsJobs = mutableListOf<Job>()

        for (dailyForecast in dailyForecasts) {
            val dailyForecastJob = launch {
                val dailyForecastId = saveDailyForecastsUseCase.execute(
                    dailyForecastDomainModel = dailyForecast,
                    cityId = cityId,
                )

                val saveHourlyForecastsJobs = mutableListOf<Job>()
                for (hourlyForecast in dailyForecast.hourlyForecasts) {
                    val hourlyForecastJob = launch {
                        saveHourlyForecastsUseCase.execute(
                            hourlyForecastDomainModel = hourlyForecast,
                            dailyForecastId = dailyForecastId
                        )
                    }
                    saveHourlyForecastsJobs.add(hourlyForecastJob)
                }
                saveHourlyForecastsJobs.joinAll()
            }
            saveDailyForecastsJobs.add(dailyForecastJob)
        }
        saveDailyForecastsJobs.joinAll()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}