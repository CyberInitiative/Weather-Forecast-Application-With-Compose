package com.example.weathercompose.ui

import com.example.weathercompose.domain.mapper.ForecastUIStateMapper
import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        ForecastViewModel(
            loadAllLocationsUseCase = get(),
            deleteLocationUseCase = get(),
            forecastUIStateMapper = get(),
            locationItemsMapper = get(),
            searchLocationUseCase = get(),
            saveLocationUseCase = get(),
            loadForecastUseCase = get(),
            saveForecastUseCase = get(),
            deleteForecastUseCase = get(),
        )
    }

    factory { ForecastUIStateMapper(context = androidApplication()) }

    factory { LocationItemMapper(context = androidApplication()) }
}