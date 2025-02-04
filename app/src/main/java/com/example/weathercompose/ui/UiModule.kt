package com.example.weathercompose.ui

import com.example.weathercompose.ui.mapper.CityUIModelMapper
import com.example.weathercompose.ui.mapper.ForecastMapper
import com.example.weathercompose.ui.viewmodel.CityManagerViewModel
import com.example.weathercompose.ui.viewmodel.CitySearchViewModel
import com.example.weathercompose.ui.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        MainViewModel(
            loadForecastUseCase = get(),
            loadCityUseCase = get(),
            cityUIModelMapper = get(),
            loadAllCitiesUseCase = get(),
        )
    }

    viewModel {
        CitySearchViewModel(
            searchCityUseCase = get(),
            saveCityUseCase = get(),
        )
    }

    viewModel {
        CityManagerViewModel(
            loadAllCitiesUseCase = get(),
            cityUIModelMapper = get(),
        )
    }

    factory { ForecastMapper(context = androidApplication()) }

    factory { CityUIModelMapper(forecastMapper = get()) }
}