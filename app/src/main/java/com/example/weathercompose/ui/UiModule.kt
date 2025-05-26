package com.example.weathercompose.ui

import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.domain.mapper.LocationUIStateMapper
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import com.example.weathercompose.ui.viewmodel.LocationSearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        ForecastViewModel(
            loadAllLocationsUseCase = get(),
            //loadLocationsUseCase = get(),
            setLocationAsHomeUseCase = get(),
            deleteLocationUseCase = get(),
            locationUIStateMapper = get(),
            locationItemsMapper = get(),
            saveLocationUseCase = get(),
            loadForecastUseCase = get(),
            loadLocationUseCase = get(),
            setCurrentTemperatureUnitUseCase = get(),
            getTemperatureUnitUseCase = get(),
            setForecastUpdateFrequencyUseCase = get(),
            getForecastUpdateFrequencyUseCase = get(),
            networkManager = get(),
        )
    }

    viewModel {
        LocationSearchViewModel(
            searchLocationUseCase = get(),
            saveLocationUseCase = get(),
            networkManager = get(),
        )
    }

    factory { LocationUIStateMapper(context = get()) }

    factory { LocationItemMapper() }
}