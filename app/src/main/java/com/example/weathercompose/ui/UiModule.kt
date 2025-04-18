package com.example.weathercompose.ui

import com.example.weathercompose.domain.mapper.ForecastUIStateMapper
import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import com.example.weathercompose.ui.viewmodel.LocationSearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        ForecastViewModel(
            loadLocationUseCase = get(),
            loadAllCitiesUseCase = get(),
            deleteLocationUseCase = get(),
            loadForecastUseCase = get(),
            saveForecastUseCase = get(),
            deleteForecastUseCase = get(),
            networkManager = get(),
            forecastUIStateMapper = get(),
            locationItemsMapper = get(),
        )
    }

    viewModel {
        LocationSearchViewModel(
            searchLocationUseCase = get(),
            saveLocationUseCase = get(),
        )
    }

    factory { ForecastUIStateMapper(context = androidApplication()) }

    factory { LocationItemMapper(context = androidApplication()) }
}