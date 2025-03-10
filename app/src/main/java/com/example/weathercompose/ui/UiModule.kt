package com.example.weathercompose.ui

import com.example.weathercompose.ui.mapper.CityMapper
import com.example.weathercompose.ui.mapper.ForecastUIStateMapper
import com.example.weathercompose.ui.viewmodel.CityManagerViewModel
import com.example.weathercompose.ui.viewmodel.CitySearchViewModel
import com.example.weathercompose.ui.viewmodel.MainViewModel
import com.example.weathercompose.ui.viewmodel.SharedViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { MainViewModel(forecastUIStateMapper = get()) }

    viewModel {
        CitySearchViewModel(
            searchCityUseCase = get(),
            saveCityUseCase = get(),
        )
    }

    viewModel {
        CityManagerViewModel(
            loadAllCitiesUseCase = get(),
            forecastUIStateMapper = get(),
            deleteForecastsUseCase = get(),
            cityMapper = get(),
        )
    }

    viewModel {
        SharedViewModel(
            loadCityUseCase = get(),
            loadAllCitiesUseCase = get(),
            loadForecastUseCase = get(),
            saveForecastsUseCase = get(),
            deleteForecastsUseCase = get(),
            networkManager = get(),
        )
    }

    // factory { ForecastMapper(context = androidApplication()) }

    factory { ForecastUIStateMapper() }

    factory { CityMapper(context = androidApplication()) }
}