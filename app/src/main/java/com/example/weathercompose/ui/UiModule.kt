package com.example.weathercompose.ui

import com.example.weathercompose.domain.mapper.LocationUIStateMapper
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
            locationUIStateMapper = get(),
            locationItemsMapper = get(),
            searchLocationUseCase = get(),
            saveLocationUseCase = get(),
            loadForecastUseCase = get(),
            loadLocationUseCase = get(),
        )
    }

    factory { LocationUIStateMapper(context = androidApplication()) }

    factory { LocationItemMapper(context = androidApplication()) }
}